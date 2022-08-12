package com.amirreza.musicplayer.features.feature_playingMusic.fragment_playing_music

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.general.EXTRA_TRACK_LIST
import com.amirreza.musicplayer.general.JetViewModel

class PlayingMusicViewModel(
    bundle: Bundle
):JetViewModel() {
    private val _trackList = MutableLiveData<List<Track>>()
    val trackList: LiveData<List<Track>> = _trackList

    private val _currentTrack = MutableLiveData<Track>()
    val currentTrack: LiveData<Track> = _currentTrack

    private val _isLikeClicked = MutableLiveData(false)
    val isLikeClicked:LiveData<Boolean> = _isLikeClicked

    private val _isRepeatClicked = MutableLiveData(false)
    val isRepeatClicked:LiveData<Boolean> = _isRepeatClicked

    private val _isTrackStatusPause = MutableLiveData(false)
    val isTrackStatusPause:LiveData<Boolean> = _isTrackStatusPause

    init {
        _trackList.value = bundle.getParcelableArrayList(EXTRA_TRACK_LIST)
        _currentTrack.value = _trackList.value!!.get(0)
    }

}