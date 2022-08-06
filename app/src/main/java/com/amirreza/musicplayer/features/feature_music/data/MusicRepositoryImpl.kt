package com.amirreza.musicplayer.features.feature_music.data

import android.content.Context
import com.amirreza.musicplayer.features.feature_music.domain.entities.Album
import com.amirreza.musicplayer.features.feature_music.domain.entities.Artist
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_music.domain.repository.MusicRepository
import com.amirreza.musicplayer.features.feature_music.presentation.MusicHelper


class MusicRepositoryImpl(private val context: Context): MusicRepository {

    override fun getTracks(): List<Track> {
        return MusicHelper.getTracks(context)
    }
    override fun extractAlbums(trackList:List<Track>): List<Album> {
        return MusicHelper.extractAlbums(trackList)
    }
    override fun extractArtists(albumList:List<Album>): List<Artist> {
        return MusicHelper.extractArtists(albumList)
    }
}