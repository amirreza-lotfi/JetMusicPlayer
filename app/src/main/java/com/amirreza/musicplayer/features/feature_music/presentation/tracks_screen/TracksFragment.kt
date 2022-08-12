package com.amirreza.musicplayer.features.feature_music.presentation.tracks_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.databinding.FragmentTracksBinding
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_music.presentation.home_screen.ItemListAdapter
import com.amirreza.musicplayer.features.feature_music.presentation.home_screen.util.OnItemClickEvent
import com.amirreza.musicplayer.general.EXTRA_TRACK_LIST
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class TracksFragment : Fragment(), OnItemClickEvent{

    lateinit var binding: FragmentTracksBinding

    private val viewModel:TrackViewModel by inject{ parametersOf(this.arguments)  }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentTracksBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.tracksRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        viewModel.trackList.observe(viewLifecycleOwner){
            binding.title.text = "Tracks (${it.size})"
            recyclerView.adapter = ItemListAdapter(this,requireContext(),it)
        }
    }

    override fun click(track: Track) {
        viewModel.trackList.value?.let {
            val bundle = Bundle()
            val trackList = viewModel.putTrackToFirst(it.toMutableList(),track)

            bundle.putParcelableArrayList(EXTRA_TRACK_LIST,trackList as ArrayList)

            findNavController().navigate(R.id.action_homeFragment_to_playingMusicFragment,bundle)
        }
    }

}