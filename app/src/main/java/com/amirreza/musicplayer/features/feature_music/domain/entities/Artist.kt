package com.amirreza.musicplayer.features.feature_music.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Artist(
    var name: String,
    var albums: MutableList<Album>,
    var songCount: Int,
    var albumCount: Int,
) : Parcelable, MusicComponentsI{
    override fun calculateDuration():Long{
        return albums.sumOf { it.duration }
    }

    override fun getTracks(): ArrayList<Track> {
        return arrayListOf<Track>().apply {
            for(album:Album in albums){
                this.addAll(album.tracks)
            }
        }
    }

    fun getAlbums():ArrayList<Album>{
        return arrayListOf<Album>().apply {
            this.addAll(albums)
        }
    }
}