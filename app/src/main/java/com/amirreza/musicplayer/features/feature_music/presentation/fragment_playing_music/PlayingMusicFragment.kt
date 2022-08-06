package com.amirreza.musicplayer.features.feature_music.presentation.fragment_playing_music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.databinding.FragmentPlayingMusicBinding
import com.amirreza.musicplayer.general.JetFragment
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_track.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class PlayingMusicFragment : JetFragment() {
    lateinit var binding: FragmentPlayingMusicBinding
    private val viewModel:PlayingMusicViewModel by inject(){ parametersOf(this.arguments)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayingMusicBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.currentTrack.observe(viewLifecycleOwner){
            setTrackName(it.trackName)
            setTrackArtist(it.artist)
            setTrackBackgroundImage(it.albumArtPic)
            setTrackMainImage(it.albumArtPic)
        }
    }

    private fun setTrackArtist(artist: String) {
        binding.songArtist.text = artist
    }

    private fun setTrackName(trackName:String) {
        binding.nameOfSong.text = trackName
        binding.songNameInMaterialCardView.text = trackName
    }

    private fun setTrackBackgroundImage(trackArtPath:String){
        Glide.with(requireContext())
            .load(trackArtPath)
            .centerCrop()
            .placeholder(R.drawable.ic_album_24)
            .into(binding.backgroundTrackImage)
    }

    private fun setTrackMainImage(trackArtPath:String){
        Glide.with(requireContext())
            .load(trackArtPath)
            .centerCrop()
            .placeholder(R.drawable.ic_album_24)
            .into(binding.trackMainImage)
    }


}