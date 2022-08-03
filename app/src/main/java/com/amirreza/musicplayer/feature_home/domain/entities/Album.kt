package com.amirreza.musicplayer.feature_home.domain.entities

data class Album(
    var albumName: String,
    var year: String,
    var artist: String,
    var duration: Long,
    var tracks: MutableList<Track>
)
