package com.amirreza.musicplayer.feature_home.presentation

sealed class HomeEvent{
    data class PermissionStatus(val boolean: Boolean): HomeEvent()
}
