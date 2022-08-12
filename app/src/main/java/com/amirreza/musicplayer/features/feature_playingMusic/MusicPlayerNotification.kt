package com.amirreza.musicplayer.features.feature_playingMusic

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_music.presentation.MusicHelper
import com.amirreza.musicplayer.features.feature_playingMusic.services.NotificationActionBroadcast
import com.amirreza.musicplayer.general.NotificationActions
import com.amirreza.musicplayer.general.NotificationConst


class MusicPlayerNotification(
    private val context:Context,
    private val notificationManager: NotificationManagerCompat
) {

    private var notificationBuilder:NotificationCompat.Builder?= null

    fun createNotification(track: Track): Notification {
        createNotificationChannel()

        notificationBuilder = NotificationCompat.Builder(context,NotificationConst.CHANNEL_ID)
        notificationBuilder!!
                .setSmallIcon(R.drawable.ic_track_24)
                .setLargeIcon(MusicHelper.getBitmapOfTrack(context,track))
                .setContentTitle(track.trackName)
                .setContentText(track.artist)
            .addAction(notificationAction(NotificationActions.PREVIOUS))
            .addAction(notificationAction(NotificationActions.PLAY_PAUSE))
            .addAction(notificationAction(NotificationActions.NEXT))
            .addAction(notificationAction(NotificationActions.CLOSE))
        notificationBuilder!!.setStyle(
            androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2)
        )

        return notificationBuilder!!.build()
    }

    fun updateNotification(newTrack:Track,isTrackPlaying:Boolean){
        notificationBuilder?.let {
            val artPic = MusicHelper.getBitmapOfTrack(context, newTrack)
            it.setOngoing(isTrackPlaying)
            it.setLargeIcon(artPic)
                .setContentTitle(newTrack.trackName)
                .setContentText(newTrack.artist)
                .setSubText(newTrack.albumName)
            NotificationManagerCompat.from(context).notify(NotificationConst.ID,it.build())
        }
    }

    @RequiresApi(26)
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.getNotificationChannel(NotificationConst.CHANNEL_ID)?.let {
                val channel = NotificationChannel(NotificationConst.CHANNEL_ID,"Jet Music",NotificationManager.IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    private fun notificationAction(action:NotificationActions): NotificationCompat.Action {
        val icon = when(action){
            NotificationActions.PLAY_PAUSE ->{
                R.drawable.ic_play
            }

            NotificationActions.NEXT ->{
                R.drawable.ic_previous
            }
            NotificationActions.PREVIOUS->{
                R.drawable.ic_previous
            }
            NotificationActions.CLOSE->{
                R.drawable.ic_close
            }
        }
        return NotificationCompat.Action.Builder(icon,"action",createPendingIntent(action.actionName)).build()
    }


    private fun createPendingIntent(actionName:String): PendingIntent {
        return PendingIntent
            .getBroadcast(context,NotificationConst.MUSIC_PLAYER_NOTIFICATION_BROADCAST_REQUEST_ID,
                Intent(context, NotificationActionBroadcast::class.java).setAction(actionName),PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

