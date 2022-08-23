package com.amirreza.musicplayer.features.feature_music.presentation.fragment_artist

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amirreza.musicplayer.features.feature_music.domain.entities.Artist
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.general.EXTRA_ALBUM_LIST
import com.amirreza.musicplayer.general.EXTRA_TRACK_LIST

class ArtistsViewModel(bundle: Bundle) :ViewModel() {
    private val _artistsList = MutableLiveData<ArrayList<Artist>>()
    val artistsList: LiveData<ArrayList<Artist>> = _artistsList

    init {
        _artistsList.value = bundle.getParcelableArrayList(EXTRA_ALBUM_LIST)
        Log.i("printing","${_artistsList.value!!.size}")
        for(art:Artist in _artistsList.value!!)
            Log.i("printing","${art.name}")
    }
}