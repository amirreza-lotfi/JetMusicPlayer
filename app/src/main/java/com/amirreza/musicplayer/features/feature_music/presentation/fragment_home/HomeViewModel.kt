package com.amirreza.musicplayer.features.feature_music.presentation.fragment_home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amirreza.musicplayer.general.JetViewModel
import com.amirreza.musicplayer.features.feature_music.domain.entities.Album
import com.amirreza.musicplayer.features.feature_music.domain.entities.Artist
import com.amirreza.musicplayer.features.feature_music.domain.entities.PlayList
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_music.domain.repository.MusicRepository
import com.amirreza.musicplayer.features.feature_music.presentation.fragment_home.util.HomeEvent

class HomeViewModel(private val musicRepository: MusicRepository):JetViewModel() {
    private val _tracksLiveData = MutableLiveData(musicRepository.getTracks())
    val tracksLiveData: LiveData<List<Track>> = _tracksLiveData

    private val _albumsLiveData = MutableLiveData(musicRepository.extractAlbums(_tracksLiveData.value!!))
    val albumsLiveData: LiveData<List<Album>> = _albumsLiveData

    private val _artistsLiveData = MutableLiveData(musicRepository.extractArtists(_albumsLiveData.value!!))
    val artistsLiveData: LiveData<List<Artist>> = _artistsLiveData

    private val _playListsLiveData = MutableLiveData<List<PlayList>>()
    val playListsLiveData: LiveData<List<PlayList>> = _playListsLiveData

    fun onEvent(event: HomeEvent){
        when(event){
            is HomeEvent.PermissionStatus ->{
                permissionNotAllowed.value = event.boolean
            }
        }
    }

    fun getTracksCount():Int{
        return _tracksLiveData.value?.size ?: 0
    }
    fun getAlbumsCount():Int{
        return _albumsLiveData.value?.size ?: 0
    }
    fun getArtistsCount():Int{
        return _artistsLiveData.value?.size ?: 0
    }
    fun getPlayListCount(): Int {
        return _playListsLiveData.value?.size ?: 0
    }

}