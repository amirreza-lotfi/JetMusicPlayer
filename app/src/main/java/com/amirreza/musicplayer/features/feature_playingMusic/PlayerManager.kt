package com.amirreza.musicplayer.features.feature_playingMusic

import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem




class PlayerManager(private val tracks: ArrayList<Track>,private val exoPlayer: ExoPlayer) {
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
        exoPlayer.play()
    }
}