package com.amirreza.musicplayer.features.feature_music.presentation.home_screen.util

sealed class HomeEvent{
    data class PermissionStatus(val boolean: Boolean): HomeEvent()
}
