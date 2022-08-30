package com.amirreza.musicplayer.features.feature_music.presentation.activity_main

import com.amirreza.musicplayer.features.feature_music.domain.entities.Track

sealed class ActivityEvent{
    data class SetIsTrackPlayingLiveData(
        val isTrackPlaying: Boolean
    ): ActivityEvent()
    data class IsTrackPlayingEvent(
        val isTrackPlaying: Boolean,
        val currentPositionOfTrack: Long
    ): ActivityEvent()
    data class OnTrackPositionChanged(val newPosition: Long):ActivityEvent()
    data class OnTrackFinished(
        val nextTrack: Track,
        val isTrackPlaying: Boolean,
        val trackPosition: Long
    ): ActivityEvent()
    object OnCloseButtonClick: ActivityEvent()
    data class OnServiceAttached(val newTrack: Track, val trackPosition: Long): ActivityEvent()
    data class PausePlayButtonClicked(val isTrackPlaying: Boolean, val trackPosition: Long): ActivityEvent()
}
