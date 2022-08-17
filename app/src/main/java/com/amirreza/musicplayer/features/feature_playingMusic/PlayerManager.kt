package com.amirreza.musicplayer.features.feature_playingMusic

import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem




class PlayerManager(private val tracks: ArrayList<Track>,private val exoPlayer: ExoPlayer) {
    private var currentTrackIndex = 0

    init {
        val mediaItems = getMediaItems()
        exoPlayer.setMediaItems(mediaItems)
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
        }
        currentTrackIndex++
    }
    fun playPrevious(){
        if(exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPrevious()
        }
        currentTrackIndex--
    }

    fun getCurrentTrack():Track = tracks[currentTrackIndex]

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

}