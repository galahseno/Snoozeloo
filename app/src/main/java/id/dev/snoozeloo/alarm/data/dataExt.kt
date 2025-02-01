package id.dev.snoozeloo.alarm.data

import id.dev.snoozeloo.alarm.data.local.AlarmEntity
import id.dev.snoozeloo.alarm.domain.AlarmModel

fun AlarmModel.toAlarmEntity() = AlarmEntity(
    id = if (id == -1) null else id,
    alarmTime = alarmTime,
    alarmName = alarmName,
    snoozedTime = snoozedTime,
    isActive = isActive,
    selectedDays = selectedDays,
    alarmRingtone = alarmRingtone,
    alarmRingtoneUri = alarmRingtoneUri,
    alarmVolume = alarmVolume,
    isVibrate = isVibrate,
)

fun AlarmEntity.toAlarmModel() = AlarmModel(
    id = id,
    alarmTime = alarmTime,
    alarmName = alarmName,
    snoozedTime =snoozedTime,
    isActive = isActive,
    selectedDays = selectedDays,
    alarmRingtone = alarmRingtone,
    alarmRingtoneUri = alarmRingtoneUri,
    alarmVolume = alarmVolume,
    isVibrate = isVibrate,
)