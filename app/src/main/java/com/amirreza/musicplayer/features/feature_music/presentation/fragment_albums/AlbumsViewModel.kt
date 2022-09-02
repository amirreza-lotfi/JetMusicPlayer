package com.amirreza.musicplayer.features.feature_music.presentation.fragment_albums

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amirreza.musicplayer.features.feature_music.domain.entities.Album
import com.amirreza.musicplayer.general.EXTRA_ALBUM_LIST

class AlbumsViewModel(
    bundle:Bundle
): ViewModel() {

    private val _albumList = MutableLiveData<ArrayList<Album>>()
    val albumList: LiveData<ArrayList<Album>> = _albumList

    init {
        _albumList.value = bundle.getParcelableArrayList(EXTRA_ALBUM_LIST)
    }
}