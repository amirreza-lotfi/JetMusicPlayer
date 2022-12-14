package com.amirreza.musicplayer.general

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Insets
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun getScreenWidth(activity: Activity): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = activity.windowManager.currentWindowMetrics
        val insets: Insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        val with = windowMetrics.bounds.width() - insets.left - insets.right
        if(with == 0)
            1
        else
            with
    } else {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val with = displayMetrics.widthPixels
        if(with == 0)
            1
        else
            with
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

fun createVerticalLinearLayoutManager(context: Context): LinearLayoutManager {
    return LinearLayoutManager(context, RecyclerView.VERTICAL,false)
}
fun createHorizontalLinearLayoutManager(context: Context): LinearLayoutManager {
    return LinearLayoutManager(context, RecyclerView.HORIZONTAL,false)
}