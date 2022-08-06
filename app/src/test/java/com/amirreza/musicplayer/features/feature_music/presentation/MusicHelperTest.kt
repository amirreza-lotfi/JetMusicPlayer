package com.amirreza.musicplayer.features.feature_music.presentation

import org.junit.Test


class MusicHelperTest{
    @Test
    fun `convert duration-- input-195269   output03-15`(){
        val time = MusicHelper.convertDurationOfMusicToNormalForm(195269)
        assert(time=="03:15")
    }

    @Test
    fun `convert duration-- input-43863   output00-43`(){
        val time = MusicHelper.convertDurationOfMusicToNormalForm(43863)
        assert(time=="03:15")
    }
}