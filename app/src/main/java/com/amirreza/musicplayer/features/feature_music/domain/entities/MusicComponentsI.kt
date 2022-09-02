package com.amirreza.musicplayer.features.feature_music.domain.entities;

interface MusicComponentsI {
    fun getTracks():ArrayList<Track>
    fun calculateDuration():Long
}
