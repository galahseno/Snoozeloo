package id.dev.snoozeloo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import id.dev.snoozeloo.alarm.data.di.dataModule
import id.dev.snoozeloo.alarm.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SnoozelooApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@SnoozelooApp)
            modules(
                dataModule,
                presentationModule
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =  NotificationChannel(
                CHANNEL_ID,
                "High priority notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.setSound(null, null)
            val notificationManager = this.getSystemService<NotificationManager>() as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "Channel_id"
    }
}