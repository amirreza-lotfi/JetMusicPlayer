package com.amirreza.musicplayer.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class JetViewModel:ViewModel() {
    val permissionNotAllowed = MutableLiveData(true)
}