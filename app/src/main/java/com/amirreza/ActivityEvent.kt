package com.amirreza

import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_playingMusic.PlayingFragmentEvent

sealed class ActivityEvent{
    data class SetIsTrackPlayingLiveData(
        val isTrackPlaying: Boolean
    ): ActivityEvent()
    data class IsTrackPlayingEvent(
        val isTrackPlaying: Boolean,
        val currentPositionOfTrack: Long
    ): ActivityEvent()
    data class OnSeekBarTouched(val position:Long): ActivityEvent()
    data class OnTrackFinished(
        val nextTrack: Track,
        val isTrackPlaying: Boolean,
        val trackPosition: Long
    ): ActivityEvent()
    data class OnNextTrackClicked(val nextTrack: Track): ActivityEvent()
    data class OnPreviousTrackClicked(val previousTrack: Track): ActivityEvent()
    data class SetCurrentTrack(val newTrack:Track): ActivityEvent()
    object OnCloseButtonClick: ActivityEvent()
    data class OnServiceAttached(val newTrack: Track, val trackPosition: Long): ActivityEvent()
    data class PausePlayButtonClicked(val isTrackPlaying: Boolean, val trackPosition: Long): ActivityEvent()
}
