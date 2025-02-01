package id.dev.snoozeloo.core.presentation

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager

object AudioPlay {
    private var mediaPlayer: MediaPlayer? = null

    fun playAudio(context: Context, uri: Uri, isLooping: Boolean = true, volume: Float? = null) {
        createMediaPlayer(context, uri, volume)

        mediaPlayer?.let {
            it.isLooping = isLooping
            it.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    private fun createMediaPlayer(context: Context, uri: Uri, volume: Float? = null) {
        mediaPlayer?.stop()

        mediaPlayer = MediaPlayer.create(context, uri)
        volume?.let {
            mediaPlayer?.setVolume(it, it)
        }
    }

    fun pauseAudio() {
        mediaPlayer?.pause()
    }

}