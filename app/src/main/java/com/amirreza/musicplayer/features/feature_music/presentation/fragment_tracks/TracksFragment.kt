package com.amirreza.musicplayer.features.feature_music.presentation.fragment_tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.databinding.ViewItemListBinding
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_music.presentation.fragment_home.ItemListAdapter
import com.amirreza.musicplayer.features.feature_music.presentation.fragment_home.util.OnItemClickEvent
import com.amirreza.musicplayer.general.EXTRA_TRACK_LIST
import com.amirreza.musicplayer.general.JetFragment
import com.amirreza.musicplayer.general.createVerticalLinearLayoutManager
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class TracksFragment : JetFragment(), OnItemClickEvent{

    lateinit var binding: ViewItemListBinding

    private val viewModel:TrackViewModel by inject{ parametersOf(this.arguments)  }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = ViewItemListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.itemRecyclerView
        recyclerView.layoutManager = createVerticalLinearLayoutManager(requireContext())
        viewModel.trackList.observe(viewLifecycleOwner){
            binding.title.text = "Tracks (${it.size})"
            recyclerView.adapter = ItemListAdapter(this,requireContext(),it)
        }
    }

    override fun <T>click(track: T) {
        viewModel.trackList.value?.let {
            val bundle = Bundle()

            bundle.putInt("2",it.indexOf(track as Track))
            bundle.putParcelableArrayList(EXTRA_TRACK_LIST,it)
            findNavController().navigate(R.id.action_homeFragment_to_playingMusicFragment,bundle)
        }
    }

}