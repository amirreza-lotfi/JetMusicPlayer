package com.amirreza.musicplayer.features.feature_music.presentation.fragment_playing_music

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.general.BUNDLE_TRACKLIST_TO_PLAYING_FRAGMENT
import com.amirreza.musicplayer.general.JetViewModel

class PlayingMusicViewModel(
    bundle: Bundle
):JetViewModel() {
    private val _trackList = MutableLiveData<List<Track>>()
    val trackList: LiveData<List<Track>> = _trackList

    init {
        _trackList.value = bundle.getParcelableArrayList(BUNDLE_TRACKLIST_TO_PLAYING_FRAGMENT)
    }

}