package com.amirreza.musicplayer.feature_home.domain.entities

data class Artist(
    var name: String,
    var albums: List<Album>,
    var songCount: Int,
    var albumCount: Int,
)
