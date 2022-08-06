package com.amirreza.musicplayer.features.feature_music.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Track(
    var artist: String = "",
    var trackName: String = "",
    var displayName: String = "",
    var albumName: String = "",
    var relativePath: String = "",
    var absolutePath: String = "",
    var albumArtPic: String = "",
    var year: Int = 0,
    var track: Int = 0,
    var startFrom: Int = 0,
    var dateAdded: Int = 0,
    var id: Long = 0,
    var duration: Long = 0,
    var albumId: Long = 0
):Parcelable