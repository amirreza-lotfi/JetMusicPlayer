package com.amirreza.musicplayer.features.feature_playingMusic.entity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

class JetMusicTimer {
    private var position = MutableLiveData(0L)
    private var job: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    fun startTimer(newPosition: Long = position.value!!) {
        job?.cancel()
        job = GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                delay(1000)
                position.postValue(position.value?.plus(1000) ?: 0)
            }
        }
    }

    fun stopTime(){
        job?.cancel()
    }

    fun getPosition():LiveData<Long>{
        return position
    }
    fun setPosition(pos: Long){
        position.postValue(pos)
    }
}