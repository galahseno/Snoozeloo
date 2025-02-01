package id.dev.snoozeloo.alarm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(TypeConverter::class)
@Database(
    entities = [AlarmEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AlarmDatabase: RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}