package com.amirreza.musicplayer.features.feature_music.presentation.tracks_screen

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.general.CARRY_TRACK_LIST
import com.amirreza.musicplayer.general.JetViewModel

class TrackViewModel(
    bundle:Bundle
):JetViewModel() {

    private val _trackList = MutableLiveData<ArrayList<Track>>()
    val trackList: LiveData<ArrayList<Track>> = _trackList

    init {
        _trackList.value = bundle.getParcelableArrayList(CARRY_TRACK_LIST)
    }

    private fun getTrackListCount():Int{
        return _trackList.value?.size ?: 0
    }
    fun putTrackToFirst(trackList: MutableList<Track>, track: Track): MutableList<Track> {
        val indexOfTrack = trackList.indexOf(track)

        val ttt = trackList[0]
        trackList[0] = trackList[indexOfTrack]
        trackList[indexOfTrack] = ttt
        return trackList
    }

}