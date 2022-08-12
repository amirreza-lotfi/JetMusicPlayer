package com.amirreza.musicplayer.features.feature_playingMusic.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_playingMusic.MusicPlayerNotification
import com.amirreza.musicplayer.features.feature_playingMusic.PlayerManager
import com.amirreza.musicplayer.general.EXTRA_TRACK_LIST
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import org.koin.android.ext.android.inject

class PlayingMusicService: Service() {
    lateinit var playerManager: PlayerManager
    lateinit var musicPlayerNotification:MusicPlayerNotification
    private val exoPlayer by inject<ExoPlayer>()

    override fun onBind(p0: Intent?): IBinder {
        return PlayingMusicBinder()
    }


    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { _intent->
            val tracks:ArrayList<Track> = _intent.getBundleExtra(EXTRA_TRACK_LIST)?.getParcelableArrayList(EXTRA_TRACK_LIST) ?: arrayListOf()
            val playerManager = PlayerManager(tracks,exoPlayer)
            val notificationManager = MusicPlayerNotification(this@PlayingMusicService,NotificationManagerCompat.from(this@PlayingMusicService))
        }
        return START_STICKY
    }
    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }


    fun playTrack(track:Track){
    }
    fun resumeTrack(){
    }
    fun playPreviousTrack(){

    }
    fun playNextTrack(){

    }



    inner class PlayingMusicBinder: Binder(){
        fun getService(): PlayingMusicService = this@PlayingMusicService
    }
}