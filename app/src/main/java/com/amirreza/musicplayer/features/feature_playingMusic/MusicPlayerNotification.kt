package com.amirreza.musicplayer.features.feature_playingMusic

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_music.presentation.MusicHelper
import com.amirreza.musicplayer.features.feature_music.presentation.activity_main.MainActivity
import com.amirreza.musicplayer.features.feature_playingMusic.services.NotificationActionBroadcast
import com.amirreza.musicplayer.general.NotificationActions
import com.amirreza.musicplayer.general.NotificationConst


class MusicPlayerNotification(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat
) {

    private var notificationBuilder: NotificationCompat.Builder? = null

    fun createNotification(track: Track): Notification {
        createNotificationChannel()
        notificationBuilder = NotificationCompat.Builder(context, NotificationConst.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_track_24)
            .setLargeIcon(MusicHelper.getBitmapOfTrack(context, track))
            .setContentTitle(track.trackName)
            .setContentText(track.artist)
            .addAction(notificationAction(NotificationActions.PREVIOUS))
            .addAction(notificationAction(NotificationActions.PLAY_PAUSE,true))
            .addAction(notificationAction(NotificationActions.NEXT))
            .addAction(notificationAction(NotificationActions.CLOSE))
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
        return notificationBuilder!!.build()
    }

    fun updateNotification(newTrack: Track, isTrackPlaying: Boolean) {
        notificationBuilder?.let {
            it.mActions[1] = notificationAction(NotificationActions.PLAY_PAUSE, isTrackPlaying)
            val artPic = MusicHelper.getBitmapOfTrack(context, newTrack)
            it.setOngoing(isTrackPlaying)
            it.setLargeIcon(artPic)
                .setContentTitle(newTrack.trackName)
                .setContentText(newTrack.artist)
                .setSubText(newTrack.albumName)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                )
            NotificationManagerCompat.from(context).notify(121221, it.build())
        }

    }

    @RequiresApi(26)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                notificationManager.getNotificationChannel(NotificationConst.CHANNEL_ID)
            if (notificationChannel == null) {
                val channel = NotificationChannel(
                    NotificationConst.CHANNEL_ID,
                    "Jet Music",
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    private fun notificationAction(action: NotificationActions, isTrackPlaying: Boolean = false): NotificationCompat.Action {
        Log.i("musicPlayerNotification","isPlaying -> $isTrackPlaying")
        val icon = when (action) {
            NotificationActions.PLAY_PAUSE -> {
                if (isTrackPlaying) R.drawable.ic_play else R.drawable.ic_baseline_pause_24
            }
            NotificationActions.NEXT -> {
                R.drawable.ic_next_media
            }
            NotificationActions.PREVIOUS -> {
                R.drawable.ic_previous_media
            }
            NotificationActions.CLOSE -> {
                R.drawable.ic_close
            }
        }
        return NotificationCompat.Action.Builder(
            icon,
            action.actionName,
            createPendingIntent(action.actionName)
        ).build()
    }

    private fun createPendingIntent(actionName: String): PendingIntent {
        return PendingIntent
            .getBroadcast(
                context,
                NotificationConst.MUSIC_PLAYER_NOTIFICATION_BROADCAST_REQUEST_ID,
                Intent(context, NotificationActionBroadcast::class.java).setAction(actionName),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
    }
}

