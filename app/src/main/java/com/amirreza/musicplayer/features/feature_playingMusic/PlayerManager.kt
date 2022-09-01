package com.amirreza.musicplayer.features.feature_playingMusic

import android.util.Log
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_playingMusic.services.PlayerListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.DISCONTINUITY_REASON_SKIP


class PlayerManager(private val tracks: ArrayList<Track>,private val exoPlayer: ExoPlayer, clickedMediaItemIndex:Int) {

    init {
        val mediaItems = getMediaItems()
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.seekTo(clickedMediaItemIndex,0)
    }

    private fun getMediaItems(): List<MediaItem> {
        val mediaItems: MutableList<MediaItem> = ArrayList()
        for (song in tracks) {
            val mediaItem: MediaItem = MediaItem.Builder()
                .setUri(song.absolutePath)
                .build()
            mediaItems.add(mediaItem)
        }
        return mediaItems
    }

    fun play(){
        exoPlayer.stop()
        exoPlayer.seekTo(0)
        exoPlayer.prepare()
        exoPlayer.play()
    }
    fun pause(){
        exoPlayer.pause()
    }
    fun isAnyTrackPlaying(): Boolean {
        return exoPlayer.isPlaying
    }
    fun resumeTrack(position:Long){
        exoPlayer.seekTo(position)
        exoPlayer.play()
    }
    fun playNextTrack(){
        if(exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNext()
            exoPlayer.seekTo(0)
        }
    }
    fun playPrevious(){
        if(exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPrevious()
        }
    }
    fun getCurrentTrack():Track = tracks[exoPlayer.currentMediaItemIndex]
    fun getTrackCurrentPosition():Long{
        if(exoPlayer.currentPosition<10)
            return exoPlayer.currentPosition
        return exoPlayer.currentPosition-3
    }
    fun hasNextTrack():Boolean{
        return exoPlayer.hasNextMediaItem()
    }
    fun hasPreviousTrack():Boolean{
        return exoPlayer.hasPreviousMediaItem()
    }
    fun seekTo(pos:Long){
        exoPlayer.seekTo(pos)
    }

    fun setListener(playerListener: PlayerListener){
        exoPlayer.addListener(object : Player.Listener{
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                when (reason) {
                    Player.DISCONTINUITY_REASON_AUTO_TRANSITION -> {
                        playerListener.onMusicPlayerFinishPlayingAllMedia()
                        playerListener.onFinishTrack(getCurrentTrack())
                    }
                    Player.DISCONTINUITY_REASON_SEEK -> {
                        playerListener.onTrackPositionChanged(newPosition.positionMs)
                    }
                }
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playerListener.isTrackPlaying(isPlaying)
            }
        })
    }

    fun getCurrentTrackIndex() = exoPlayer.currentMediaItemIndex

    fun release(){
        exoPlayer.stop()
    }

}