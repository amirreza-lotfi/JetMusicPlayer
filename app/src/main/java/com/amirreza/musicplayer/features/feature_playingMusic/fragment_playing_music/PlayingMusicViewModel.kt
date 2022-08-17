package com.amirreza.musicplayer.features.feature_playingMusic.fragment_playing_music

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_playingMusic.PlayingFragmentEvent
import com.amirreza.musicplayer.general.EXTRA_TRACK_LIST
import com.amirreza.musicplayer.general.JetViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayingMusicViewModel(
    bundle: Bundle
) : JetViewModel() {
    private val _trackList = MutableLiveData<List<Track>>()
    val trackList: LiveData<List<Track>> = _trackList

    private val _currentTrack = MutableLiveData<Track>()
    val currentTrack: LiveData<Track> = _currentTrack

    private val _trackPosition = MutableLiveData(0L)
    val trackPosition: LiveData<Long> = _trackPosition

    private val _isLikeClicked = MutableLiveData(false)
    val isLikeClicked: LiveData<Boolean> = _isLikeClicked

    private val _isRepeatClicked = MutableLiveData(false)
    val isRepeatClicked: LiveData<Boolean> = _isRepeatClicked

    private val _isTrackStatusPause = MutableLiveData(false)
    val isTrackStatusPause: LiveData<Boolean> = _isTrackStatusPause

    private val _isTrackPlaying = MutableLiveData(true)
    val isTrackPlaying: LiveData<Boolean> = _isTrackPlaying

    private var durationTimerJob: Job? = null

    init {
        _trackList.value = bundle.getParcelableArrayList(EXTRA_TRACK_LIST)
        _currentTrack.value = _trackList.value!![0]
    }

    fun startTrackPosition(initValue: Long = trackPosition.value!!) {
        _trackPosition.postValue(initValue)

        durationTimerJob?.cancel()
        durationTimerJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(1000)
                _trackPosition.postValue(trackPosition.value?.plus(1000L) ?: 0)
            }
        }
    }

    private fun stopTimer() {
        durationTimerJob?.cancel()
    }

    fun onUiEvent(event: PlayingFragmentEvent) {
        when (event) {
            is PlayingFragmentEvent.SetIsTrackPlayingLiveData -> {
                _isTrackPlaying.value = event.isTrackPlaying
            }
            is PlayingFragmentEvent.OnSeekBarTouched -> {
                if(isTrackPlaying.value == true){
                    startTrackPosition(event.position)
                }else{
                    durationTimerJob?.cancel()
                    _trackPosition.value = event.position
                }
            }
            is PlayingFragmentEvent.OnTrackFinished->{
                startTrackPosition(0)
                _isTrackPlaying.value = true
                _currentTrack.value = event.nextTrack
            }
            is PlayingFragmentEvent.PausePlayButtonClicked->{
                if(_isTrackPlaying.value!!){
                    stopTimer()
                    _isTrackPlaying.value = false
                }else{
                    _isTrackPlaying.value = true
                    startTrackPosition()
                }
            }
            is PlayingFragmentEvent.OnNextTrackClicked->{
                _currentTrack.value = event.nextTrack
                startTrackPosition(0)
            }
            is PlayingFragmentEvent.OnPreviousTrackClicked->{
                _currentTrack.value = event.previousTrack
                startTrackPosition(0)
            }
        }
    }

}