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
    private var rememberedTrackSeekbarPosition = 0L

    override fun onBind(p0: Intent?): IBinder {
        return PlayingMusicBinder()
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { _intent->
            val tracks:ArrayList<Track> = _intent.getParcelableArrayListExtra(EXTRA_TRACK_LIST) ?: arrayListOf()
            playerManager = PlayerManager(tracks,exoPlayer)
            musicPlayerNotification = MusicPlayerNotification(this@PlayingMusicService,NotificationManagerCompat.from(this@PlayingMusicService))
            musicPlayerNotification.createNotification(tracks[0])
        }
        playTrack()
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }


    fun playTrack(){
        playerManager.play()
        musicPlayerNotification.updateNotification(playerManager.getCurrentTrack(),playerManager.isAnyTrackPlaying())
    }
    fun pauseTrack(){
        rememberedTrackSeekbarPosition = playerManager.getTrackPausedPosition()
        playerManager.pause()
        musicPlayerNotification.updateNotification(playerManager.getCurrentTrack(),playerManager.isAnyTrackPlaying())
    }
    fun resumeTrack(){
        if(rememberedTrackSeekbarPosition==0L){
            playerManager.play()
        }else{
            playerManager.resumeTrack(rememberedTrackSeekbarPosition)
            musicPlayerNotification.updateNotification(playerManager.getCurrentTrack(),playerManager.isAnyTrackPlaying())
        }
    }
    fun playPreviousTrack(){
        playerManager.playPrevious()
        musicPlayerNotification.updateNotification(playerManager.getCurrentTrack(),playerManager.isAnyTrackPlaying())
    }
    fun playNextTrack(){
        playerManager.playNextTrack()
        musicPlayerNotification.updateNotification(playerManager.getCurrentTrack(),playerManager.isAnyTrackPlaying())
    }

    fun isTrackPlaying():Boolean{
        return playerManager.isAnyTrackPlaying()
    }


    inner class PlayingMusicBinder: Binder(){
        fun getService(): PlayingMusicService = this@PlayingMusicService
    }
}