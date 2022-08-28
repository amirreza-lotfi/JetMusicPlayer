package com.amirreza.musicplayer.features.feature_music.presentation.fragment_home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.databinding.FragmentHomeBinding
import com.amirreza.musicplayer.features.feature_music.domain.entities.Album
import com.amirreza.musicplayer.features.feature_music.domain.entities.Artist
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_music.presentation.fragment_home.components.HomeActionItem
import com.amirreza.musicplayer.features.feature_music.presentation.fragment_home.util.OnItemClickEvent
import com.amirreza.musicplayer.general.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : JetFragment(),OnItemClickEvent{
    lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tracksItem.setOnClickListener {
            viewModel.tracksLiveData.value?.let {
                val bundle = Bundle()
                bundle.putParcelableArrayList(EXTRA_TRACK_LIST,it as ArrayList)
                findNavController().navigate(R.id.action_homeFragment_to_tracksFragment,bundle)
            }
        }

        binding.albumItem.setOnClickListener {
            viewModel.albumsLiveData.value?.let{
                val bundle = Bundle()
                val arrayList = arrayListOf<Album>().apply {
                    this.addAll(it)
                }
                bundle.putParcelableArrayList(EXTRA_ALBUM_LIST,arrayList)
                findNavController().navigate(R.id.action_homeFragment_to_albumsFragment,bundle)
            }
        }

        binding.artistItem.setOnClickListener {
            viewModel.artistsLiveData.value?.let {
                val bundle = Bundle()
                val arrayList = arrayListOf<Artist>().apply {
                    this.addAll(it)
                }
                bundle.putParcelableArrayList(EXTRA_ALBUM_LIST,arrayList)
                findNavController().navigate(R.id.action_homeFragment_to_artistsFragment,bundle)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        setUpPermissions()
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
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var result = true
        permissions.entries.forEach{
            result = it.value && result
        }
        Log.i("homeFragment","requestPermissionLauncher: $result" )

        if(result == true)
            setUpUi()
    }


    private fun setUpUi(){
        setTracksCountInUi()
        setAlbumsCountInUi()
        setArtistsCountInUi()
        setPlayListCountInUi()

        val recyclerView = binding.itemRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        viewModel.tracksLiveData.observe(viewLifecycleOwner){
            recyclerView.adapter = ItemListAdapter(this,requireContext(),it!!)
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

    override fun <T> click(item: T) {
        viewModel.tracksLiveData.value?.let {
            val bundle = Bundle()

            bundle.putInt("2",it.indexOf(item as Track))
            bundle.putParcelableArrayList(EXTRA_TRACK_LIST,it as ArrayList)
            findNavController().navigate(R.id.action_homeFragment_to_playingMusicFragment,bundle)
        }
    }

}