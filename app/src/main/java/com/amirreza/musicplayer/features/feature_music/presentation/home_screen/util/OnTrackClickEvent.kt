package com.amirreza.musicplayer.features.feature_music.presentation.home_screen.util

import com.amirreza.musicplayer.features.feature_music.domain.entities.Track

interface OnTrackClickEvent {
    fun click(track: Track)
}