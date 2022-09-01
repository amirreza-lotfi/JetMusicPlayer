package com.amirreza.musicplayer.features.feature_music.presentation.activity_main

import com.amirreza.musicplayer.features.feature_music.domain.entities.Track

sealed class ActivityEvent {
    data class OnTrackFinished(val isTrackPlaying: Boolean, val trackPosition: Long) :
        ActivityEvent()

    object OnCloseButtonClick : ActivityEvent()
    data class PausePlayButtonClicked(val isTrackPlaying: Boolean, val trackPosition: Long) :
        ActivityEvent()

    object FragmentChangedToPlayingFragment : ActivityEvent()
    object FragmentChangedToOtherFragments : ActivityEvent()
    object PlayingServiceIsNotAvailable : ActivityEvent()
    data class PlayingServiceIsAlive(
        val currentTrack: Track,
        val trackPosition: Long,
        val isTrackPlaying: Boolean
    ) : ActivityEvent()

    data class NotificationNextAction(val trackPosition: Long) : ActivityEvent()
    data class NotificationPreviousAction(val trackPosition: Long) : ActivityEvent()
    object NotificationCloseAction: ActivityEvent()
    object AllTracksFinished: ActivityEvent()
    data class NotificationPlayPauseAction(val trackPosition: Long, val isTrackPlaying: Boolean) : ActivityEvent()

}
