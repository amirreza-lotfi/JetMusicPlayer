package com.amirreza.musicplayer.general

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class JetViewModel:ViewModel() {
    val permissionAllowed = MutableLiveData(false)
}