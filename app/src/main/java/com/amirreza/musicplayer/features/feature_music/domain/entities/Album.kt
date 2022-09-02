package com.amirreza.musicplayer.features.feature_music.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Album(
    var albumName: String,
    var year: String,
    var artistName: String,
    var duration: Long,
    var tracks: MutableList<Track>
):Parcelable, MusicComponentsI{

    override fun getTracks(): ArrayList<Track> {
        return arrayListOf<Track>().apply {
            this.addAll(tracks)
        }
    }
    override fun calculateDuration(): Long {
        return duration
    }
}
