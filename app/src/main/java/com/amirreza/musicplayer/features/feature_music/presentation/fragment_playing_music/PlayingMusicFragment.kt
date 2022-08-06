package com.amirreza.musicplayer.features.feature_music.presentation.fragment_playing_music

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.databinding.FragmentPlayingMusicBinding


class PlayingMusicFragment : Fragment() {
    lateinit var binding: FragmentPlayingMusicBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayingMusicBinding.inflate(inflater,container,false)
        return binding.root
    }


}