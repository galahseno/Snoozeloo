package id.dev.snoozeloo.alarm.data.di

import androidx.room.Room
import id.dev.snoozeloo.alarm.data.AlarmRepositoryImpl
import id.dev.snoozeloo.alarm.data.local.AlarmDatabase
import id.dev.snoozeloo.alarm.domain.AlarmRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            AlarmDatabase::class.java,
            "snoozeloo.db"
        )
            .build()
    }

    single { get<AlarmDatabase>().alarmDao() }
    singleOf(::AlarmRepositoryImpl).bind<AlarmRepository>()
}