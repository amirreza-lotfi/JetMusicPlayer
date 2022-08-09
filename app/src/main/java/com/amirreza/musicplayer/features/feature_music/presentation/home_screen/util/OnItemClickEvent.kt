package com.amirreza.musicplayer.features.feature_music.presentation.home_screen.util

import com.amirreza.musicplayer.features.feature_music.domain.entities.Track

interface OnItemClickEvent {
    fun <T>click(item: T)
}