package com.amirreza.musicplayer.features.feature_playingMusic.services

import com.amirreza.musicplayer.features.feature_music.domain.entities.Track

interface PlayerListener{
    fun onFinishTrack(nextTrack: Track)
    fun onMusicPlayerFinishPlayingAllMedia()
    fun isTrackPlaying(boolean: Boolean)
    fun onTrackPositionChanged(newPosition:Long)
}