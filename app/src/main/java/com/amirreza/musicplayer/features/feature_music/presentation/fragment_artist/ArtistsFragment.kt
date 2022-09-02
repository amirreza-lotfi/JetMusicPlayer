package com.amirreza.musicplayer.features.feature_music.presentation.fragment_artist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.databinding.ViewItemListBinding
import com.amirreza.musicplayer.features.feature_music.domain.entities.Album
import com.amirreza.musicplayer.features.feature_music.domain.entities.Artist
import com.amirreza.musicplayer.features.feature_music.presentation.fragment_home.ItemListAdapter
import com.amirreza.musicplayer.features.feature_music.presentation.fragment_home.util.OnItemClickEvent
import com.amirreza.musicplayer.features.feature_music.presentation.fragment_tracks.TrackViewModel
import com.amirreza.musicplayer.general.EXTRA_ALBUM_LIST
import com.amirreza.musicplayer.general.createVerticalLinearLayoutManager
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ArtistsFragment : Fragment(), OnItemClickEvent {
    lateinit var binding: ViewItemListBinding

    private val viewModel: ArtistsViewModel by inject { parametersOf(this.arguments) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ViewItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = "Artists (${viewModel.artistsList.value!!.size})"


        val recyclerView = binding.itemRecyclerView
        recyclerView.layoutManager = createVerticalLinearLayoutManager(requireContext())

        viewModel.artistsList.observe(viewLifecycleOwner) {
            recyclerView.adapter = ItemListAdapter(this, requireContext(), it)
        }
    }

    override fun <T> click(item: T) {
        val bundle = Bundle().apply {
            val albumsOfClickedArtist = (item as Artist).getAlbums()
            this.putParcelableArrayList(EXTRA_ALBUM_LIST, albumsOfClickedArtist)
        }
        findNavController().navigate(R.id.action_artistsFragment_to_albumsFragment, bundle)
    }

}