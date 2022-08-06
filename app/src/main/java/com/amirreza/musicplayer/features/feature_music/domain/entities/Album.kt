package com.amirreza.musicplayer.features.feature_music.domain.entities

data class Album(
    var albumName: String,
    var year: String,
    var artistName: String,
    var duration: Long,
    var tracks: MutableList<Track>
)
