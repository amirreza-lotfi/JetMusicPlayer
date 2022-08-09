package com.amirreza.musicplayer.features.feature_player_service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class PlayingMusicService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")

    }
    override fun onCreate() {
        super.onCreate()
    }
    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
}