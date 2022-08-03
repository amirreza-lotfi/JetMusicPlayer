package com.amirreza.musicplayer.feature_home.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amirreza.musicplayer.RESPONSE_OF_PERMISSION_REQUEST
import com.amirreza.musicplayer.base.JetFragment
import com.amirreza.musicplayer.databinding.FragmentHomeBinding


class HomeFragment : JetFragment() {
    lateinit var binding: FragmentHomeBinding

    private val viewModel = MusicViewModel()

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

    }
}