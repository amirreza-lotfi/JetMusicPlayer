package com.amirreza.musicplayer.feature_home

sealed class HomeEvent{
    data class PermissionStatus(val boolean: Boolean):HomeEvent()
}
