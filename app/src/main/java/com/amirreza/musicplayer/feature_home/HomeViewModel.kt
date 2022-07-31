package com.amirreza.musicplayer.feature_home

import androidx.lifecycle.ViewModel
import com.amirreza.musicplayer.base.JetViewModel

class HomeViewModel:JetViewModel() {

    fun onEvent(event: HomeEvent){
        when(event){
            is HomeEvent.PermissionStatus->{
                permissionNotAllowed.value = event.boolean
            }
        }
    }
}