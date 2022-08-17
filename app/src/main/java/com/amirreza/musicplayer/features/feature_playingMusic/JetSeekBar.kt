package com.amirreza.musicplayer.features.feature_playingMusic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import com.amirreza.musicplayer.R
import kotlin.math.min
import kotlin.math.roundToInt

@SuppressLint("ClickableViewAccessibility")
class JetSeekBar(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var view: View
    private var sliderFill: FrameLayout
    private var cursor: FrameLayout

    private var maxValue = 0.0
    var currentValue = 0.0
    private var widthOfView = 0

    var cursorWidth=0
    var cursorHeight=0


    init {
        view = inflate(context, R.layout.view_jet_seekbar, this)
        sliderFill = view.findViewById(R.id.slider_fill)
        cursor = view.findViewById(R.id.slider_curser)
    }

    fun setValues(max:Long, current:Long, width: Int){
        maxValue = max.toDouble()
        currentValue = current.toDouble()
        widthOfView = width.toInt()

        cursorWidth = convertDpToPixel(12f,context!!).toInt()
        cursorHeight = convertDpToPixel(12f,context!!).toInt()

    }

    private fun onChangedCurrentValue(newWidth: Int) {
        if (newWidth != 0 && maxValue != 0.0) {
            val layoutParams = sliderFill.layoutParams
            layoutParams.width = newWidth
            sliderFill.layoutParams = layoutParams


            val layoutParamsCursor = LayoutParams(cursorWidth,cursorHeight)
            layoutParamsCursor.marginStart = newWidth
            cursor.layoutParams = layoutParamsCursor
        }
    }

    fun updateSeekbarByNewValue(newValue: Long){
        val durationToWidth = (newValue.toDouble()/maxValue)*widthOfView
        onChangedCurrentValue(durationToWidth.toInt())
    }

    fun setOnSeekbarTouchedListener(onSeekbarEvent: OnSeekbarEvent) {
        view.setOnTouchListener { view, motionEvent ->
            val touchedWidth = motionEvent.x

            onChangedCurrentValue(touchedWidth.toInt())

            val currentValueChanged = (touchedWidth / widthOfView) * (maxValue)
            onSeekbarEvent.onCurrentPositionChanged(currentValueChanged.toLong())

            true
        }
    }

}

fun convertDpToPixel(dp: Float, context: Context?): Float {
    return if (context != null) {
        val resources = context.resources
        val metrics = resources.displayMetrics
        dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    } else {
        val metrics = Resources.getSystem().displayMetrics
        dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}

interface OnSeekbarEvent {
    fun onCurrentPositionChanged(touchedPosition: Long)
    fun onTrackFinished()
}