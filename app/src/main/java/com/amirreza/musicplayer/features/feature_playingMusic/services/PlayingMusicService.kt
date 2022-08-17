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
            startForeground(121221, musicPlayerNotification.createNotification(tracks[0]))
        }
        playTrack()
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }


    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }


    private fun playTrack(){
        playerManager.play()
        musicPlayerNotification.updateNotification(playerManager.getCurrentTrack(),playerManager.isAnyTrackPlaying())
    }

    fun pauseTrack(){
        rememberedTrackSeekbarPosition = playerManager.getTrackCurrentPosition()
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
    fun playPreviousTrack(): Track {
        playerManager.playPrevious()
        musicPlayerNotification.updateNotification(playerManager.getCurrentTrack(),playerManager.isAnyTrackPlaying())
        return playerManager.getCurrentTrack()
    }
    fun playNextTrack(): Track {
        playerManager.playNextTrack()
        musicPlayerNotification.updateNotification(playerManager.getCurrentTrack(),playerManager.isAnyTrackPlaying())
        return playerManager.getCurrentTrack()
    }

    fun isTrackPlaying():Boolean{
        return playerManager.isAnyTrackPlaying()
    }

    fun getCurrentPositionOfTrack():Long{
        return playerManager.getTrackCurrentPosition()
    }

    fun seekTrackTo(pos:Long){
        rememberedTrackSeekbarPosition = pos
        return playerManager.seekTo(pos)
    }

    fun hasNextTrack():Boolean{
        return playerManager.hasNextTrack()
    }
    fun hasPreviousTrack():Boolean{
        return playerManager.hasPreviousTrack()
    }

    inner class PlayingMusicBinder: Binder(){
        fun getService(): PlayingMusicService = this@PlayingMusicService
    }
}