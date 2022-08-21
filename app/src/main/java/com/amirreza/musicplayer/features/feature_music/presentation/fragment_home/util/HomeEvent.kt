package com.amirreza.musicplayer.features.feature_music.presentation.fragment_home.util

sealed class HomeEvent{
    data class PermissionStatus(val boolean: Boolean): HomeEvent()
}
