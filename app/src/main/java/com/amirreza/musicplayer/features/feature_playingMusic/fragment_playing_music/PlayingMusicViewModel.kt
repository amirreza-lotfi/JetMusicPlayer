package com.amirreza.musicplayer.features.feature_playingMusic.fragment_playing_music

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_playingMusic.PlayingFragmentEvent
import com.amirreza.musicplayer.general.EXTRA_TRACK_CLICKED_POSITION
import com.amirreza.musicplayer.general.EXTRA_TRACK_LIST
import com.amirreza.musicplayer.general.JetViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayingMusicViewModel: JetViewModel() {

    val _currentTrack = MutableLiveData<Track>()
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

    var indexOfSelectedItem = 0

    private var durationTimerJob: Job? = null

    fun startTimer(trackPos: Long = trackPosition.value!!) {
        _trackPosition.postValue(trackPos)

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
                if (event.isTrackPlaying) {
                    startTimer(event.currentPositionOfTrack)
                } else {
                    durationTimerJob?.cancel()
                    _trackPosition.value = event.currentPositionOfTrack
                }
            }
            is PlayingFragmentEvent.OnSeekBarTouched -> {
                _trackPosition.value = event.position
            }

            is PlayingFragmentEvent.OnTrackFinished -> {
                startTimer(0)
                _isTrackPlaying.value = true
                _currentTrack.value = event.nextTrack

            }
            is PlayingFragmentEvent.PausePlayButtonClicked -> {
                if (event.isPlaying) {
                    stopTimer()
                } else {
                    startTimer()
                }
            }
            is PlayingFragmentEvent.OnNextTrackClicked -> {
                startTimer(0)
                _currentTrack.value = event.nextTrack
            }
            is PlayingFragmentEvent.OnPreviousTrackClicked -> {
                startTimer(0)
                _currentTrack.value = event.previousTrack
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}