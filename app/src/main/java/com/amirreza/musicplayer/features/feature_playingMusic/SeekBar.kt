package com.amirreza.musicplayer.features.feature_playingMusic

class SeekBar(private val  jetSeekBar: JetSeekBar,max:Long, current:Long){

    fun setOnSeekbarTouchedListener(onSeekbarEvent: OnSeekbarEvent) {
        jetSeekBar.setOnSeekbarTouchedListener(onSeekbarEvent)
    }

    fun updateSeekbarByNewValue(it:Long){
        jetSeekBar.updateSeekbarByNewValue(it)
    }

    init {
        val width = jetSeekBar.measuredWidth
        jetSeekBar.setValues(max,current,width)
    }

}