package id.dev.snoozeloo.core.presentation.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import id.dev.snoozeloo.alarm.domain.AlarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class AlarmRebootReceiver : BroadcastReceiver() {
    private var serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val alarmRepository by inject<AlarmRepository>(AlarmRepository::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            serviceScope.launch {
                context?.let {
                    val alarmList = alarmRepository.getAlarms().first()

                    alarmList.filter { it.isActive }.forEach { alarm ->
                        alarm.id?.let {
                            AlarmService().setDailyReminder(context, it, alarm.alarmTime)
                        }
                    }
                }
            }
        }
    }
}