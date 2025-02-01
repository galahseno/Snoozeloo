package id.dev.snoozeloo.core.presentation

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager


object AudioPlay {
    private var mediaPlayer: MediaPlayer? = null

    fun playAudio(context: Context, uri: Uri, isLooping: Boolean = true, volume: Float? = null) {
        pauseAudio()

        mediaPlayer = MediaPlayer()

        mediaPlayer?.let {
            it.isLooping = isLooping
            it.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
            it.setAudioAttributes(AudioAttributes.Builder().setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .setLegacyStreamType(AudioManager.STREAM_ALARM)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
            it.setDataSource(context, uri)

            volume?.let { volume ->
                mediaPlayer?.setVolume(volume, volume)
            }

            it.prepare()
            it.start()
        }
    }

    fun pauseAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}