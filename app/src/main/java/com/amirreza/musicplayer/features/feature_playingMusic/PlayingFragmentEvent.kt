package com.amirreza.musicplayer.features.feature_playingMusic

import com.amirreza.musicplayer.features.feature_music.domain.entities.Track

sealed class PlayingFragmentEvent{
    data class SetIsTrackPlayingLiveData(
        val isTrackPlaying: Boolean,
        val currentPositionOfTrack: Long
    ):PlayingFragmentEvent()
    data class OnSeekBarTouched(val position:Long):PlayingFragmentEvent()
    data class OnTrackFinished(val nextTrack: Track):PlayingFragmentEvent()
    data class OnNextTrackClicked(val nextTrack: Track):PlayingFragmentEvent()
    data class OnPreviousTrackClicked(val previousTrack: Track):PlayingFragmentEvent()
    object PausePlayButtonClicked:PlayingFragmentEvent()
}
