package com.amirreza.musicplayer.features.feature_playingMusic.services

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.media.session.MediaSession
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_playingMusic.MusicPlayerNotification
import com.amirreza.musicplayer.features.feature_playingMusic.PlayerManager
import com.amirreza.musicplayer.general.EXTRA_TRACK_CLICKED_POSITION
import com.amirreza.musicplayer.general.EXTRA_TRACK_LIST
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import org.koin.android.ext.android.inject


class PlayingMusicService: Service() {
    private lateinit var tracks:ArrayList<Track>
    private lateinit var playerManager: PlayerManager
    private lateinit var musicPlayerNotification:MusicPlayerNotification
    private val exoPlayer by inject<ExoPlayer>()
    private var rememberedTrackSeekbarPosition = 0L

    override fun onBind(p0: Intent?): IBinder {
        return PlayingMusicBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { _intent->
            tracks = _intent.getParcelableArrayListExtra(EXTRA_TRACK_LIST) ?: arrayListOf()
            val indexOfCurrentTrack = _intent.getIntExtra(EXTRA_TRACK_CLICKED_POSITION,0)
            playerManager = PlayerManager(tracks,exoPlayer,indexOfCurrentTrack)
            musicPlayerNotification = MusicPlayerNotification(this@PlayingMusicService,NotificationManagerCompat.from(this@PlayingMusicService))
            startForeground(121221, musicPlayerNotification.createNotification(tracks[0]))
        }
        playTrack()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
        stopForeground(true)
        Log.i("123456789","destroy called")
    }

    fun getCurrentTrack():Track{
        return playerManager.getCurrentTrack()
    }
    fun playTrack(){
        musicPlayerNotification.updateNotification(playerManager.getCurrentTrack(),playerManager.isAnyTrackPlaying())
        playerManager.play()
    }
    fun pauseTrack(){
        musicPlayerNotification.updateNotification(playerManager.getCurrentTrack(),playerManager.isAnyTrackPlaying())
        rememberedTrackSeekbarPosition = playerManager.getTrackCurrentPosition()
        playerManager.pause()
    }
    fun resumeTrack(){
        musicPlayerNotification.updateNotification(playerManager.getCurrentTrack(),playerManager.isAnyTrackPlaying())

        if(rememberedTrackSeekbarPosition==0L){
            playerManager.play()
        }else{
            playerManager.resumeTrack(rememberedTrackSeekbarPosition)
        }
    }
    fun playPreviousTrack(): Track {
        musicPlayerNotification.updateNotification(playerManager.getCurrentTrack(),playerManager.isAnyTrackPlaying())
        playerManager.playPrevious()
        return playerManager.getCurrentTrack()
    }
    fun playNextTrack(): Track {
        musicPlayerNotification.updateNotification(playerManager.getCurrentTrack(),playerManager.isAnyTrackPlaying())
        playerManager.playNextTrack()
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
    fun onPlayingTrackListener(playerListener: PlayerListener){
        playerManager.setListener(playerListener)
    }

    inner class PlayingMusicBinder: Binder(){
        fun getService(): PlayingMusicService = this@PlayingMusicService
    }

    fun getCurrentTrackIndex(): Int {
        return playerManager.getCurrentTrackIndex()
    }
    fun release(){
        playerManager.release()
        stopForeground(true)
        stopSelf()
    }
}

