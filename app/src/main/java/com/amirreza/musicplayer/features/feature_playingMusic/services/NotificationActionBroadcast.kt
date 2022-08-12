package com.amirreza.musicplayer.features.feature_playingMusic.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.amirreza.musicplayer.general.NotificationConst.NOTIFICATION_ACTION_BROADCAST

class NotificationActionBroadcast:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let{ _context->
            intent?.let {  _intent->
                _context.sendBroadcast(Intent(NOTIFICATION_ACTION_BROADCAST).putExtra("actionName",_intent.action))
            }
        }
    }
}

