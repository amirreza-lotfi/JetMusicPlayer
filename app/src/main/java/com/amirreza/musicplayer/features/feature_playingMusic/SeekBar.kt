package com.amirreza.musicplayer.features.feature_playingMusic

class SeekBar(private val  jetSeekBar: JetSeekBar,max:Long, current:Long){

    fun setOnSeekbarTouchedListener(onSeekbarEvent: OnSeekbarEvent) {
        jetSeekBar.setOnSeekbarTouchedListener(onSeekbarEvent)
    }

    init {
        val width = jetSeekBar.measuredWidth
        jetSeekBar.setValues(max,current,width)
    }

}