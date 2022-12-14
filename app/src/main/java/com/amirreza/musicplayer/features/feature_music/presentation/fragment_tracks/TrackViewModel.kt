package com.amirreza.musicplayer.features.feature_music.presentation.fragment_tracks

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.general.EXTRA_TRACK_LIST
import com.amirreza.musicplayer.general.JetViewModel

class TrackViewModel(
    bundle:Bundle
):JetViewModel() {

    private val _trackList = MutableLiveData<ArrayList<Track>>()
    val trackList: LiveData<ArrayList<Track>> = _trackList

    init {
        _trackList.value = bundle.getParcelableArrayList(EXTRA_TRACK_LIST)
    }
}