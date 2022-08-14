package com.amirreza.musicplayer.features.feature_playingMusic

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.marginStart
import com.amirreza.musicplayer.R
import kotlin.math.min
import kotlin.math.roundToInt

@SuppressLint("ClickableViewAccessibility")
class JetSeekBar(context: Context, attrs:AttributeSet?):FrameLayout(context,attrs){
    private var view: View
    private var sliderFill:FrameLayout
    private var cursor:FrameLayout

    private var maxValue = 0.0
    private var currentValue = 0.0
    private var widthOfView = 0

    fun setMaxValue(value:Long){
        maxValue = value.toDouble()
    }
    fun setCurrentValue(value: Long){
        currentValue = value.toDouble()
    }

    fun onChangedCurrentValue(newValue:Long){
        val widthOfFillSlider = ((newValue / maxValue) * widthOfView).roundToInt()
        val layoutParams = LayoutParams(widthOfFillSlider, widthOfFillSlider)
        sliderFill.layoutParams = layoutParams

        layoutParams.marginStart = widthOfFillSlider
        cursor.layoutParams = layoutParams
    }

    fun setOnSeekbarTouchedListener(onSeekbarEvent: OnSeekbarEvent) {
        view.setOnTouchListener { view, motionEvent ->
            val touchedWidth = motionEvent.x

            onChangedCurrentValue(touchedWidth.toLong())

            val currentValueChanged = (touchedWidth / widthOfView) * (maxValue)
            onSeekbarEvent.onCurrentPositionChanged(currentValueChanged.toLong())

            true
        }
    }


    init {

        view = inflate(context, R.layout.view_jet_seekbar, this)
        sliderFill = view.findViewById(R.id.slider_fill)
        cursor = view.findViewById(R.id.slider_curser)

        attrs?.let {
            val getAttrs = context.obtainStyledAttributes(attrs, R.styleable.JetSeekBar)

            widthOfView = view.measuredWidth
            maxValue = getAttrs.getString(R.styleable.JetSeekBar_maxValue)?.toDouble() ?: 0.0
            currentValue = getAttrs.getString(R.styleable.JetSeekBar_currentValue)?.toDouble() ?: 0.0

            onChangedCurrentValue(currentValue.toLong())

        }
    }

}

interface OnSeekbarEvent{
    fun onCurrentPositionChanged(touchedPosition:Long)
}