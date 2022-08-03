package com.amirreza.musicplayer.feature_home.domain.repository

import com.amirreza.musicplayer.feature_home.domain.entities.Album
import com.amirreza.musicplayer.feature_home.domain.entities.Artist
import com.amirreza.musicplayer.feature_home.domain.entities.Track

interface MusicRepository {
    fun getTracks():List<Track>
    fun extractAlbums(trackList:List<Track>):List<Album>
    fun extractArtists(trackList:List<Track>):List<Artist>
}