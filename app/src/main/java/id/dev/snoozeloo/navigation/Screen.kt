package id.dev.snoozeloo.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    object AlarmHome {
        @Serializable
        data object AlarmList
    }

    @Serializable
    object AlarmSettingsRoute {
        @Serializable
        data class AlarmSettings(
            @Serializable
            val id: Int = -1
        )

        @Serializable
        data object RingtoneSettings
    }
}