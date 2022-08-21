package com.amirreza.musicplayer.general

const val RESPONSE_OF_PERMISSION_REQUEST = 0
const val EXTRA_TRACK_LIST = "iiij0@234567wd86e6d2esweqxvxqed"
const val EXTRA_ALBUM_LIST = "@23ui4567wd86e6d2sdfghwevsd"
const val EXTRA_TRACK_CLICKED_POSITION = "34567890987654"


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