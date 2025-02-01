package id.dev.snoozeloo.core.presentation

import java.time.DayOfWeek
import java.util.Calendar

enum class RepeatDayEnum(val day:Int) {
    Mo(Calendar.MONDAY),
    Tu(Calendar.TUESDAY),
    We(Calendar.WEDNESDAY),
    Th(Calendar.THURSDAY),
    Fr(Calendar.FRIDAY),
    Sa(Calendar.SATURDAY),
    Su(Calendar.SUNDAY);

    fun toDayOfWeek(): DayOfWeek {
        return when (this) {
            Mo -> DayOfWeek.MONDAY
            Tu -> DayOfWeek.TUESDAY
            We -> DayOfWeek.WEDNESDAY
            Th -> DayOfWeek.THURSDAY
            Fr -> DayOfWeek.FRIDAY
            Sa -> DayOfWeek.SATURDAY
            Su -> DayOfWeek.SUNDAY
        }
    }
}