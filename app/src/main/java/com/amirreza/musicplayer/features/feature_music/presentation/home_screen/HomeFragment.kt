package com.amirreza.musicplayer.features.feature_music.presentation.home_screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.general.RESPONSE_OF_PERMISSION_REQUEST
import com.amirreza.musicplayer.general.JetFragment
import com.amirreza.musicplayer.databinding.FragmentHomeBinding
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_music.presentation.home_screen.components.HomeActionItem
import com.amirreza.musicplayer.features.feature_music.presentation.home_screen.util.OnTrackClickEvent
import com.amirreza.musicplayer.general.BUNDLE_TRACKLIST_TO_PLAYING_FRAGMENT
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : JetFragment(),OnTrackClickEvent{
    lateinit var binding: FragmentHomeBinding

    private val viewModel: MusicViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPermissions()

        viewModel.permissionNotAllowed.observe(viewLifecycleOwner){ mustShow->
            showPermissionNotAllowedView(mustShow)
        }

        setTracksCountInUi()
        setAlbumsCountInUi()
        setArtistsCountInUi()
        setPlayListCountInUi()
    }

    private fun setUpPermissions(){
        if(hasPermissionsAllowed()){
            setUpUi()
        }else{
            askStoragePermissions()

        }
    }

    private fun hasPermissionsAllowed():Boolean{
        val isReadFilePermissionAllowed =  ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val isWriteFilePermissionAllowed =  ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return isReadFilePermissionAllowed && isWriteFilePermissionAllowed
    }
    private fun askStoragePermissions(){
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
            RESPONSE_OF_PERMISSION_REQUEST
        )
    }

    private fun setUpUi(){
        val recyclerView = binding.tracksRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        viewModel.tracksLiveData.observe(viewLifecycleOwner){
            recyclerView.adapter = TracksAdapter(this,requireContext(),it!!)
        }

    }

    private fun setTracksCountInUi(){
        val track: HomeActionItem = binding.tracksItem
        track.setItemCount(viewModel.getTracksCount())
    }
    private fun setAlbumsCountInUi(){
        binding.albumItem.setItemCount(viewModel.getAlbumsCount())
    }

    private fun setArtistsCountInUi(){
        binding.artistItem.setItemCount(viewModel.getArtistsCount())
    }

    private fun setPlayListCountInUi(){
        binding.playListItem.setItemCount(viewModel.getPlayListCount())
    }

    override fun click(track: Track) {
        viewModel.tracksLiveData.value?.let {
            val bundle = Bundle()
            val trackList = viewModel.putTrackToFirst(it.toMutableList(),track)

            bundle.putParcelableArrayList(BUNDLE_TRACKLIST_TO_PLAYING_FRAGMENT,trackList as ArrayList)

            findNavController().navigate(R.id.action_homeFragment_to_playingMusicFragment,bundle)
        }

    }
}