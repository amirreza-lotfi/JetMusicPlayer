package com.amirreza.musicplayer.features.feature_music.presentation.activity_main

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_playingMusic.entity.JetMusicTimer
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {

    private val _showingLandingFragment = MutableLiveData(true)
    val showingLandingFragment: LiveData<Boolean> = _showingLandingFragment

    private val _trackDuration = MutableLiveData(JetMusicTimer())

    private val _isTrackPlaying = MutableLiveData(false)
    val isTrackPlaying: LiveData<Boolean> = _isTrackPlaying

    private val _bottomPlayingMustShow = MutableLiveData(false)
    val bottomPlayingMustShow: LiveData<Boolean> = _bottomPlayingMustShow

    private var showingLandingPageTimeJob: Job? = null

    init {
        showingLandingPageTimer()
    }

    fun onEvent(event: ActivityEvent) {
        when (event) {
            is ActivityEvent.NotificationPreviousAction -> {
                _trackDuration.value?.startTimer(event.trackPosition)
            }
            is ActivityEvent.NotificationNextAction -> {
                _trackDuration.value?.startTimer(event.trackPosition)
            }
            is ActivityEvent.OnCloseButtonClick -> {
                _bottomPlayingMustShow.value = false
                _trackDuration.value?.stopTime()
            }
            is ActivityEvent.NotificationCloseAction -> {
                _bottomPlayingMustShow.value = false
                _trackDuration.value?.stopTime()
            }
            is ActivityEvent.NotificationPlayPauseAction -> {
                if (event.isTrackPlaying) {
                    _trackDuration.value?.stopTime()
                } else {
                    _trackDuration.value?.startTimer(event.trackPosition)
                }
            }
            is ActivityEvent.FragmentChangedToOtherFragments -> {

            }
            is ActivityEvent.PausePlayButtonClicked -> {
                val isTrackPlaying = event.isTrackPlaying
                val trackPosition = event.trackPosition

                _isTrackPlaying.value = !isTrackPlaying

                if (isTrackPlaying) {
                    _trackDuration.value?.stopTime()
                } else {
                    _trackDuration.value?.startTimer(trackPosition)
                }
            }
            is ActivityEvent.FragmentChangedToPlayingFragment -> {
                _bottomPlayingMustShow.value = false
                _trackDuration.value?.stopTime()
            }
            is ActivityEvent.PlayingServiceIsNotAvailable -> {
                _bottomPlayingMustShow.value = false
                _trackDuration.value?.stopTime()
            }
            is ActivityEvent.PlayingServiceIsAlive -> {
                val isTrackPlaying = event.isTrackPlaying
                _bottomPlayingMustShow.value = true
                _isTrackPlaying.value = isTrackPlaying //update iconOfTrack
                if (isTrackPlaying) {
                    _trackDuration.value?.startTimer(event.trackPosition)
                } else {
                    _trackDuration.value?.setPosition(event.trackPosition)
                    _trackDuration.value?.stopTime()
                }
            }
            is ActivityEvent.OnTrackFinished -> {
                if (event.isTrackPlaying) {
                    _trackDuration.value?.startTimer(event.trackPosition)
                } else {
                    _trackDuration.value?.setPosition(event.trackPosition)
                    _trackDuration.value?.stopTime()
                }
            }
            is ActivityEvent.AllTracksFinished->{
                _trackDuration.value?.stopTime()
            }
        }
    }

    fun observeToPositionOfTrack(): LiveData<Long> {
        return _trackDuration.value?.getPosition() ?: MutableLiveData(0L)
    }

    override fun onCleared() {
        super.onCleared()
        _trackDuration.value?.stopTime()
    }

    private fun showingLandingPageTimer() {
        Log.i("MainActivityViewModel", "mustShowLanding: ${_showingLandingFragment.value}")
        if(_showingLandingFragment.value==true) {
            showingLandingPageTimeJob = viewModelScope.launch(Dispatchers.IO) {
                var timer = 0
                while (true) {
                    if (timer <= 3) {
                        timer += 1
                        delay(800)
                    } else {
                        withContext(Dispatchers.Main){
                            _showingLandingFragment.value = false
                        }
                        break
                    }
                }
                cancel()
            }
        }
    }
}