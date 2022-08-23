package com.amirreza.musicplayer.general

const val RESPONSE_OF_PERMISSION_REQUEST = 0
const val EXTRA_TRACK_LIST = "iiij0@23d2esweqxvxqed"
const val EXTRA_ALBUM_LIST = "23usd"
const val EXTRA_Artist_LIST = "23uihwevsd"
const val EXTRA_TRACK_CLICKED_POSITION = "34560987654"


object NotificationConst{
    const val ID: Int = 0
    const val CHANNEL_ID = "music_channel"
    const val ACTION_INTENT = 0
    const val MUSIC_PLAYER_NOTIFICATION_BROADCAST_REQUEST_ID = 0
    const val NOTIFICATION_ACTION_BROADCAST = "NOTIFICATIONACTIONBROADCAST"
}

enum class NotificationActions(val actionName:String){
    PLAY_PAUSE("play,pause"),NEXT("next"),PREVIOUS("previous"),CLOSE("close")
}