package com.amirreza.musicplayer.feature_home.presentation

import com.amirreza.musicplayer.base.JetViewModel

class MusicViewModel:JetViewModel() {

    fun onEvent(event: HomeEvent){
        when(event){
            is HomeEvent.PermissionStatus ->{
                permissionNotAllowed.value = event.boolean
            }
        }
    }
}