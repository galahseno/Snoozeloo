package id.dev.snoozeloo.core.presentation.alarm

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import id.dev.snoozeloo.R
import id.dev.snoozeloo.SnoozelooApp.Companion.CHANNEL_ID
import id.dev.snoozeloo.alarm.domain.AlarmModel
import id.dev.snoozeloo.alarm.domain.AlarmRepository
import id.dev.snoozeloo.alarm.presentation.trigger.AlarmTriggerActivity
import id.dev.snoozeloo.core.presentation.AudioPlay
import id.dev.snoozeloo.core.presentation.RepeatDayEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class AlarmService : Service() {
    private val notificationManager by lazy {
        getSystemService<NotificationManager>()!!
    }

    private var serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val alarmRepository by inject<AlarmRepository>(AlarmRepository::class.java)

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val id = intent.getIntExtra(EXTRA_ID, 0)
                if (applicationContext != null) {
                    serviceScope.launch {
                        val alarmSettings = alarmRepository.getAlarmById(id)

                        start(applicationContext, alarmSettings)

                        if (alarmSettings.isActive) {
                            scheduleNextAlarm(applicationContext, alarmSettings)
                        }
                    }
                }
            }

            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    private fun start(context: Context, alarmSettings: AlarmModel) {
        alarmSettings.id?.let { id ->
            val fullScreenIntent = Intent(context, AlarmTriggerActivity::class.java).apply {
                putExtra(EXTRA_ID, id)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val fullScreenPendingIntent = PendingIntent.getActivity(
                context,
                id,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val dismissIntent = Intent(context, AlarmService::class.java).apply {
                putExtra(EXTRA_ID, id)
                action = ACTION_START
            }

            val dismissPendingIntent = PendingIntent.getForegroundService(
                context,
                id,
                dismissIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setOngoing(true)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle(
                    alarmSettings.alarmName.ifEmpty {
                        "Your Alarm Triggered"
                    }
                )
                .setDeleteIntent(dismissPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setFullScreenIntent(fullScreenPendingIntent, true)

            if (alarmSettings.isVibrate) {
                builder.setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            }

            if (alarmSettings.alarmRingtoneUri.isNotEmpty()) {
                AudioPlay.playAudio(
                    context,
                    uri = Uri.parse(alarmSettings.alarmRingtoneUri),
                    volume = alarmSettings.alarmVolume
                )
            }

            startForeground(alarmSettings.id, builder.build())
            notificationManager.notify(id, builder.build())
        }
    }

    private fun stop() {
        stopSelf()
        serviceScope.cancel()
        serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

    fun setDailyReminder(context: Context, id: Int, alarmTime: String) {
        val alarmManager = context.getSystemService<AlarmManager>() ?: return

        val intent = Intent(context, AlarmService::class.java).apply {
            putExtra(EXTRA_ID, id)
            action = ACTION_START
        }

        val timeInMillis = getAlarmTimeInMillis(alarmTime)

        val pendingIntent = PendingIntent.getForegroundService(
            context, id, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent
        )
    }

    fun cancelAlarm(context: Context, id: Int) {
        val alarmManager = context.getSystemService<AlarmManager>() ?: return

        val intent = Intent(context, AlarmService::class.java).apply {
            putExtra(EXTRA_ID, id)
        }

        val pendingIntent = PendingIntent.getForegroundService(
            context, id, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            pendingIntent.cancel()
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun scheduleNextAlarm(context: Context, alarmModel: AlarmModel) {
        val alarmManager = context.getSystemService<AlarmManager>() ?: return

        alarmModel.id?.let {
            val intent = Intent(context, AlarmService::class.java).apply {
                putExtra(EXTRA_ID, alarmModel.id)
            }

            val pendingIntent = PendingIntent.getForegroundService(
                context,
                alarmModel.id,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val now = ZonedDateTime.now()
            val alarmLocalTime = parseTimeToLocalTime(alarmModel.alarmTime)
            val nextAlarmDateTime =
                findNextAlarmDateTime(now, alarmLocalTime, alarmModel.selectedDays)

            val durationUntilAlarm = Duration.between(now, nextAlarmDateTime)
            val alarmTimeInMillis = System.currentTimeMillis() + durationUntilAlarm.toMillis()

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTimeInMillis,
                pendingIntent
            )
        }
    }

    private fun findNextAlarmDateTime(
        now: ZonedDateTime,
        alarmLocalTime: LocalTime,
        selectedDays: List<Int>
    ): ZonedDateTime {
        val selectedDayEnums = selectedDays.mapNotNull { day ->
            RepeatDayEnum.entries.find { it.day == day }
        }
        val nextAlarmCandidates = mutableListOf<ZonedDateTime>()
        for (dayEnum in selectedDayEnums) {
            var alarmDateTime = now.with(dayEnum.toDayOfWeek()).with(alarmLocalTime)
            if (alarmDateTime.isBefore(now) || alarmDateTime.isEqual(now)) {
                alarmDateTime = alarmDateTime.plusWeeks(1)
            }
            nextAlarmCandidates.add(alarmDateTime)
        }
        return nextAlarmCandidates.minOrNull() ?: now.plusWeeks(1).with(alarmLocalTime)
    }

    private fun getAlarmTimeInMillis(
        alarmTime: String, zoneId: ZoneId = ZoneId.systemDefault()
    ): Long {
        val now = ZonedDateTime.now(zoneId)
        val alarmDateTime = now.with(parseTimeToLocalTime(alarmTime))
            .let { if (it.isBefore(now)) it.plusDays(1) else it }
        return alarmDateTime.toInstant().toEpochMilli()
    }

    private fun parseTimeToLocalTime(timeString: String): LocalTime {
        val formatters = listOf(
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ROOT),
            DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT)
        )

        for (formatter in formatters) {
            try {
                return LocalTime.parse(timeString, formatter)
            } catch (e: DateTimeParseException) {
                // Ignore the exception and try the next formatter
            }
        }

        throw DateTimeParseException("Unable to parse time string: $timeString", timeString, 0)
    }

    companion object {
        const val EXTRA_ID: String = "EXTRA_ID"

        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"

        fun createStopIntent(context: Context): Intent {
            return Intent(context, AlarmService::class.java).apply {
                action = ACTION_STOP
            }
        }
    }
}