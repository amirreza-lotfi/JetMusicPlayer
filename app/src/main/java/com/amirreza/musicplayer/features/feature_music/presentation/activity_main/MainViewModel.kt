package com.amirreza.musicplayer.features.feature_music.presentation.activity_main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_playingMusic.entity.JetMusicTimer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val _currentTrack = MutableLiveData<Track>()
    val currentTrack: LiveData<Track> = _currentTrack

    private val _showingLandingFragment = MutableLiveData(true)
    val showingLandingFragment:LiveData<Boolean> = _showingLandingFragment

    val _trackDuration = MutableLiveData(JetMusicTimer())
    val trackPosition: LiveData<JetMusicTimer> = _trackDuration

    val _isTrackPlaying = MutableLiveData(false)
    val isTrackPlaying: LiveData<Boolean> = _isTrackPlaying

    private var showingLandingPageTimeJob: Job? = null


    fun onEvent(event: ActivityEvent){
        when(event){
            is ActivityEvent.OnServiceAttached -> {
                _currentTrack.value = event.newTrack
                _trackDuration.value?.setPosition(event.trackPosition)
            }
            is ActivityEvent.SetIsTrackPlayingLiveData ->{
                _isTrackPlaying.value = event.isTrackPlaying
            }
            is ActivityEvent.IsTrackPlayingEvent ->{
                val isTrackPlaying = event.isTrackPlaying
                val positionOfTrack = event.currentPositionOfTrack

                if(isTrackPlaying){
                    _trackDuration.value?.startTimer(positionOfTrack)
                }else{
                    _trackDuration.value?.stopTime()
                }
            }
            is ActivityEvent.OnTrackFinished ->{
                val isTrackPlaying = event.isTrackPlaying
                val newTrack = event.nextTrack
                val trackPosition = event.trackPosition

                _currentTrack.postValue(newTrack)
                if(isTrackPlaying){
                    _trackDuration.value?.startTimer(trackPosition)
                }else{
                    _trackDuration.value?.stopTime()
                }
            }
            is ActivityEvent.PausePlayButtonClicked ->{
                val isTheTrackPlaying = event.isTrackPlaying

                if(isTheTrackPlaying){
                    //stop playing track
                    _isTrackPlaying.postValue(false)
                    _trackDuration.value?.stopTime()
                }else{
                    //resume playing track
                    _isTrackPlaying.postValue(false)
                    _trackDuration.value?.startTimer(event.trackPosition)
                }
            }
            is ActivityEvent.OnCloseButtonClick ->{
                _trackDuration.value?.stopTime()
            }
            is ActivityEvent.OnTrackPositionChanged->{
                _trackDuration.value?.startTimer(event.newPosition)
            }
            else -> {}
        }
    }

    fun observeToPositionOfTrack():LiveData<Long>{
        return _trackDuration.value?.getPosition() ?: MutableLiveData(0L)
    }
    fun getCurrentTrackDurationAsDouble():Double{
        return _currentTrack.value?.duration?.toDouble() ?: 1.0
    }

    override fun onCleared() {
        super.onCleared()
        _trackDuration.value?.stopTime()
    }

    fun showingLandingPageTimer(){
        showingLandingPageTimeJob = viewModelScope.launch(Dispatchers.IO) {
            var timer = 0
            while(true){
                if(timer<=3){
                    timer+=1
                    delay(1000)
                }else{
                    _showingLandingFragment.postValue(false)
                    showingLandingPageTimeJob?.cancel()
                }
            }
        }
    }
}