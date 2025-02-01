package id.dev.snoozeloo.alarm.data.local

import androidx.room.TypeConverter

class TypeConverter {
    @TypeConverter
    fun fromListInt(list: List<Int>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toListInt(data: String): List<Int> {
        if (data.isEmpty()) {
            return listOf()
        }
        return listOf(*data.split(",").map { it.toInt() }.toTypedArray())
    }
}
