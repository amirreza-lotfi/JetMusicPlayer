package com.amirreza.musicplayer.features.feature_music.presentation.activity_main

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.navigation.*
import androidx.navigation.fragment.findNavController
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.databinding.ActivityMainBinding
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_playingMusic.fragment_playing_music.PlayingMusicFragment
import com.amirreza.musicplayer.features.feature_playingMusic.services.PlayerListener
import com.amirreza.musicplayer.features.feature_playingMusic.services.PlayingMusicService
import com.amirreza.musicplayer.general.EXTRA_TRACK_LIST
import com.amirreza.musicplayer.general.NotificationConst.NOTIFICATION_ACTION_BROADCAST
import com.amirreza.musicplayer.general.getScreenWidth

class MainActivity : AppCompatActivity() {
    companion object {
        var playingMusicService: PlayingMusicService? = null
    }

    lateinit var binding: ActivityMainBinding
    val viewModel = MainViewModel()
    var screenWidth: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screenWidth = getScreenWidth(this)

        getNotificationAction()

    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, PlayingMusicService::class.java)

        navigateToHomeFragmentAfterThreeSecond()

        observeToChangingFragment{ destination ->
            Log.i("mainActivity40001",destination)
            if(destinationIsPlayingFragment(destination)) {
                viewModel.onEvent(ActivityEvent.FragmentChangedToPlayingFragment)
                viewModel._trackDuration.value?.stopTime()
                binding.bottomTrackPlayer.visibility = GONE
                Log.i("mainActivity40001","destination is Playing Fragment")
            }
            else {
                //service exist
                Log.i("mainActivity40001","destination is not Playing Fragment")
                if (playingMusicService == null){
                    binding.bottomTrackPlayer.visibility = GONE
                    viewModel._trackDuration.value?.stopTime()
                    Log.i("mainActivity40001","service is null")

                }else{
                    Log.i("mainActivity40001","service is not null")
                    binding.bottomTrackPlayer.visibility = VISIBLE
                    viewModel._currentTrack.value = playingMusicService!!.getCurrentTrack() //update information of track on bottom
                    viewModel._isTrackPlaying.value = playingMusicService!!.isTrackPlaying() //update iconOfTrack
                    setTrackDetailToBottomSeekbar(viewModel._currentTrack.value!!)
                    if(playingMusicService!!.isTrackPlaying()){
                        viewModel._trackDuration.value?.startTimer(playingMusicService?.getCurrentPositionOfTrack() ?: 0L)
                    }else{
                        viewModel._trackDuration.value?.setPosition(playingMusicService?.getCurrentPositionOfTrack() ?: 0L)
                        viewModel._trackDuration.value?.stopTime()
                    }
                    viewModel.observeToPositionOfTrack().observe(this@MainActivity){
                        updateBottomPlayerSeekbar(it)
                    }
                }
                playingMusicService?.let {
                    it.onPlayingTrackListener(object : PlayerListener{
                        override fun onFinishTrack(nextTrack: Track) {
                            setTrackDetailToBottomSeekbar(viewModel._currentTrack.value!!)
                            viewModel._currentTrack.value = playingMusicService!!.getCurrentTrack() //update information of track on bottom
                            viewModel._isTrackPlaying.value = playingMusicService!!.isTrackPlaying() //update iconOfTrack
                            if(playingMusicService!!.isTrackPlaying()){
                                viewModel._trackDuration.value?.startTimer(playingMusicService?.getCurrentPositionOfTrack() ?: 0L)
                            }else{
                                viewModel._trackDuration.value?.setPosition(playingMusicService?.getCurrentPositionOfTrack() ?: 0L)
                                viewModel._trackDuration.value?.stopTime()
                            }
                        }

                        override fun onMusicPlayerFinishPlayingAllMedia() {

                        }

                        override fun isTrackPlaying(boolean: Boolean) {
                            TODO("Not yet implemented")
                        }

                        override fun onTrackPositionChanged(newPosition: Long) {

                        }

                    })
                }
            }
        }

        viewModel.isTrackPlaying.observe(this) {
            setUpPlayingOrPauseButtonImageResource(it)
        }

        binding.playPauseBtn.setOnClickListener {
            val isTrackPlaying = playingMusicService?.isTrackPlaying() ?: false
            val trackPosition = playingMusicService?.getCurrentPositionOfTrack() ?: 0L

            viewModel._isTrackPlaying.value = !isTrackPlaying

            if (isTrackPlaying) {
                playingMusicService?.pauseTrack()
                viewModel._trackDuration.value?.stopTime()
            }
            else {
                playingMusicService?.resumeTrack()
                viewModel._trackDuration.value?.startTimer(trackPosition)
            }

            //viewModel.onEvent(ActivityEvent.PausePlayButtonClicked(isTrackPlaying, trackPosition))
        }

        binding.closeServiceBtn.setOnClickListener {
            binding.bottomTrackPlayer.visibility = GONE
            viewModel._trackDuration.value?.stopTime()
            playingMusicService?.release()
            playingMusicService = null
        }

        binding.bottomTrackPlayer.setOnClickListener {
            Log.i("qwertyuiop", "${playingMusicService == null}")
            findNavController(R.id.fragmentContainerView).navigate(R.id.action_homeFragment_to_playingMusicFragment)
        }

    }

    private fun setUpPlayingOrPauseButtonImageResource(isTrackPlaying: Boolean) {
        if (isTrackPlaying) {
            binding.playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            binding.playPauseBtn.setImageResource(R.drawable.ic_play)
        }
    }

    private fun setTrackDetailToBottomSeekbar(track: Track) {
        playingMusicService?.let { it ->
            val currentTitle = it.getCurrentTrack()
            binding.artistName.text = currentTitle.artist
            binding.trackTitle.text = currentTitle.trackName
        }
    }

    private fun updateBottomPlayerSeekbar(trackPosition: Long) {
        val withOfSeekbar =
            (trackPosition / viewModel.getCurrentTrackDurationAsDouble()) * screenWidth

        val layoutParams = binding.bottomSeekbar.layoutParams
        layoutParams.width = withOfSeekbar.toInt()
        binding.bottomSeekbar.layoutParams = layoutParams
    }

    private fun getNotificationAction() {
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(_context: Context?, _intent: Intent?) {

            }
        }, IntentFilter(NOTIFICATION_ACTION_BROADCAST))
    }

    private fun destinationIsPlayingFragment(destination: String): Boolean {
        return (destination == "com.amirreza.musicplayer:id/playingMusicFragment")
    }

    private fun navigateToHomeFragmentAfterThreeSecond() {
        viewModel.showingLandingFragment.observe(this) {
            findNavController(R.id.fragmentContainerView).navigate(R.id.action_landingFragment_to_homeFragment)
        }
    }

    private fun observeToChangingFragment(onDo: (destination: String) -> Unit) {
        findNavController(R.id.fragmentContainerView).addOnDestinationChangedListener { controller, destination, arguments ->
            onDo(destination.displayName)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playingMusicService?.release()
    }
}