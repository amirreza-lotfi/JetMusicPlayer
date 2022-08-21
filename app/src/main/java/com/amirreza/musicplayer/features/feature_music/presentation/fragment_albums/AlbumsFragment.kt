package com.amirreza.musicplayer.features.feature_music.presentation.fragment_albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.databinding.ViewItemListBinding
import com.amirreza.musicplayer.features.feature_music.presentation.fragment_home.ItemListAdapter
import com.amirreza.musicplayer.features.feature_music.presentation.fragment_home.util.OnItemClickEvent
import com.amirreza.musicplayer.general.EXTRA_TRACK_LIST
import com.amirreza.musicplayer.general.JetFragment
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class AlbumsFragment : JetFragment(),OnItemClickEvent {
    lateinit var binding:ViewItemListBinding
    private val viewModel:AlbumsViewModel by inject(){ parametersOf(this.arguments)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = ViewItemListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = "Albums(${viewModel.albumList.value!!.size})"
        val rv = binding.tracksRecyclerView
        rv.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        viewModel.albumList.observe(viewLifecycleOwner){
            it?.let {
                rv.adapter = ItemListAdapter(this,requireContext(),it)
            }
        }
    }

    override fun <T> click(item: T) {
        viewModel.albumList.value?.let {
            val bundle = Bundle()

        }
    }

}