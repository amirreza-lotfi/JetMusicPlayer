package com.amirreza.musicplayer.features.feature_music.domain.repository

import com.amirreza.musicplayer.features.feature_music.domain.entities.Album
import com.amirreza.musicplayer.features.feature_music.domain.entities.Artist
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track

interface MusicRepository {
    fun getTracks():List<Track>
    fun extractAlbums(trackList:List<Track>):List<Album>
    fun extractArtists(albumList:List<Album>):List<Artist>
}