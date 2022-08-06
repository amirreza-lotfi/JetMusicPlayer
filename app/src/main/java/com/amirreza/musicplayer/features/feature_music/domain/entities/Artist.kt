package com.amirreza.musicplayer.features.feature_music.domain.entities

data class Artist(
    var name: String,
    var albums: MutableList<Album>,
    var songCount: Int,
    var albumCount: Int,
)
