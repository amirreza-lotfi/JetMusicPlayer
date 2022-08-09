package com.amirreza.musicplayer.features.feature_player_service

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
import com.amirreza.musicplayer.general.NotificationConst

class MusicPlayerNotification(
    private val context:Context,
    private val notificationManager: NotificationManager
) {

    private var notificationBuilder:NotificationCompat.Builder?= null

    private fun createNotification(track: Track): Notification {
        createNotificationChannel()

        //todo openPlayerIntent

        notificationBuilder = NotificationCompat.Builder(context,NotificationConst.CHANNEL_ID)
        notificationBuilder!!
                .setSmallIcon(R.drawable.ic_track_24)
                .setLargeIcon(MusicHelper.getBitmapOfTrack(context,track))
                .setContentTitle(track.trackName)
                .setContentText(track.artist)
            .addAction(notificationAction(NotificationAction.PREVIOUS))
            .addAction(notificationAction(NotificationAction.PLAY_PAUSE))
            .addAction(notificationAction(NotificationAction.NEXT))
            .addAction(notificationAction(NotificationAction.CLOSE))
        notificationBuilder!!.setStyle(
            androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2)
        )

        return notificationBuilder!!.build()
    }

    private fun updateNotification(newTrack:Track,isTrackPlaying:Boolean){
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

    private fun notificationAction(action:NotificationAction): NotificationCompat.Action {
        val icon = when(action){
            NotificationAction.PLAY_PAUSE ->{
                R.drawable.ic_play
            }

            NotificationAction.NEXT ->{
                R.drawable.ic_previous
            }
            NotificationAction.PREVIOUS->{
                R.drawable.ic_previous
            }
            NotificationAction.CLOSE->{
                R.drawable.ic_close
            }
        }
        return NotificationCompat.Action.Builder(icon,"action",playerActionIntent(action)).build()
    }

    private fun playerActionIntent(action:NotificationAction):PendingIntent{
        val intent = Intent()
        intent.action = action.actionName
        return PendingIntent.getBroadcast(context,NotificationConst.ACTION_INTENT,intent,PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

enum class NotificationAction(val actionName:String){
    PLAY_PAUSE("play,pause"),NEXT("next"),PREVIOUS("previous"),CLOSE("close")
}