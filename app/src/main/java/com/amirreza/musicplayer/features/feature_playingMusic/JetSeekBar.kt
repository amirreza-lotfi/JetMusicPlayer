package com.amirreza.musicplayer.features.feature_playingMusic

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.general.convertDpToPixel

@SuppressLint("ClickableViewAccessibility")
class JetSeekBar(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var slider: View
    private var fillAreaView: FrameLayout
    private var cursor: FrameLayout

    var durationOfTrack = 0.0
    var currentPositionOfTrack = 0.0
    private var widthOfView = 0

    var roundedCursorWidth=0
    var roundedCursorHeight=0


    init {
        slider = inflate(context, R.layout.view_jet_seekbar, this)
        fillAreaView = slider.findViewById(R.id.slider_fill)
        cursor = slider.findViewById(R.id.slider_curser)
    }

    fun setValues(max:Long, current:Long, width: Int){
        durationOfTrack = max.toDouble()
        currentPositionOfTrack = current.toDouble()
        widthOfView = width

        roundedCursorWidth = convertDpToPixel(12f,context!!).toInt()
        roundedCursorHeight = convertDpToPixel(12f,context!!).toInt()

        updateSeekbarByNewTrackPosition(current)
    }

    private fun redrawFillAreaAndCursorInView(newWidth: Int) {
        if (durationOfTrack != 0.0) {
            val layoutParams = fillAreaView.layoutParams
            layoutParams.width = newWidth
            fillAreaView.layoutParams = layoutParams


            val layoutParamsCursor = LayoutParams(roundedCursorWidth,roundedCursorHeight)
            layoutParamsCursor.marginStart = newWidth
            cursor.layoutParams = layoutParamsCursor
        }
    }

    fun updateSeekbarByNewTrackPosition(newValue: Long){
        currentPositionOfTrack = newValue.toDouble()
        var durationToWidth = (newValue.toDouble()/durationOfTrack)*widthOfView
        if(newValue >= durationOfTrack)
            durationToWidth = 0.0
        redrawFillAreaAndCursorInView(durationToWidth.toInt())
    }

    fun setOnSeekbarTouchedListener(onSeekbarEvent: OnSeekbarEvent) {
        slider.setOnTouchListener { view, motionEvent ->
            val touchedWidth = motionEvent.x

            redrawFillAreaAndCursorInView(touchedWidth.toInt())

            val currentValueChanged = (touchedWidth / widthOfView) * (durationOfTrack)
            onSeekbarEvent.onCurrentPositionChanged(currentValueChanged.toLong())

            true
        }
    }

}