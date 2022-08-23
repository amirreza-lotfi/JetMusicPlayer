package com.amirreza.musicplayer.features.feature_music.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Artist(
    var name: String,
    var albums: MutableList<Album>,
    var songCount: Int,
    var albumCount: Int,
) : Parcelable{
    fun calculateDuration():Long{
        var du = 0L
        for(album:Album in albums){
            du += album.duration
        }
        return du
    }
}