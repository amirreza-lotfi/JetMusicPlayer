package com.amirreza.musicplayer.features.feature_music.presentation.activity_main

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.navigation.*
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.databinding.ActivityMainBinding
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_playingMusic.services.PlayerListener
import com.amirreza.musicplayer.features.feature_playingMusic.services.PlayingMusicService
import com.amirreza.musicplayer.general.NotificationActions
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

        navigateToHomeFragmentAfterThreeSecond()

        viewModel.bottomPlayingMustShow.observe(this) { mustShow ->
            binding.bottomTrackPlayer.visibility = if (mustShow) VISIBLE else GONE
        }



        observeToChangingFragment { destination ->

            if (destinationIsPlayingFragment(destination)) {
                viewModel.onEvent(ActivityEvent.FragmentChangedToPlayingFragment)
            } else {
                if (playingMusicService == null) {
                    viewModel.onEvent(ActivityEvent.PlayingServiceIsNotAvailable)
                } else {
                    viewModel.onEvent(ActivityEvent.PlayingServiceIsAlive(playingMusicService!!.getCurrentTrack(), playingMusicService!!.getCurrentPositionOfTrack(), playingMusicService!!.isTrackPlaying()))
                    setTrackDetailToBottomSeekbar()
                    viewModel.observeToPositionOfTrack().observe(this@MainActivity) {
                        updateBottomPlayerSeekbar(it)
                    }
                }

                playingMusicService?.onPlayingTrackListener(object : PlayerListener {
                    override fun onFinishTrack(nextTrack: Track) {
                        playingMusicService?.let { service ->
                            setTrackDetailToBottomSeekbar()
                            val isTrackPlaying = service.isTrackPlaying()
                            val trackPosition = service.getCurrentPositionOfTrack()
                            viewModel.onEvent(ActivityEvent.OnTrackFinished(isTrackPlaying, trackPosition))
                        }
                    }
                    override fun onMusicPlayerFinishPlayingAllMedia() {}
                    override fun isTrackPlaying(boolean: Boolean) {}
                    override fun onTrackPositionChanged(newPosition: Long) {}
                })
            }
        }

        viewModel.isTrackPlaying.observe(this) {
            setUpPlayingOrPauseButtonImageResource(it)
        }

        binding.playPauseBtn.setOnClickListener {
            val isTrackPlaying = playingMusicService?.isTrackPlaying() ?: false
            val trackPosition = playingMusicService?.getCurrentPositionOfTrack() ?: 0L

            viewModel.onEvent(ActivityEvent.PausePlayButtonClicked(isTrackPlaying,trackPosition))

            if (isTrackPlaying) {
                playingMusicService?.pauseTrack()
            } else {
                playingMusicService?.resumeTrack()
            }
        }

        binding.closeServiceBtn.setOnClickListener {
            viewModel.onEvent(ActivityEvent.OnCloseButtonClick)
            playingMusicService?.release()
            playingMusicService = null
        }

        binding.bottomTrackPlayer.setOnClickListener {
            findNavController(R.id.fragmentContainerView).navigate("playingFragmentRoute")
        }

    }

    private fun setUpPlayingOrPauseButtonImageResource(isTrackPlaying: Boolean) {
        if (isTrackPlaying) {
            binding.playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            binding.playPauseBtn.setImageResource(R.drawable.ic_play)
        }
    }

    private fun setTrackDetailToBottomSeekbar() {
        playingMusicService?.let { it ->
            val currentTitle = it.getCurrentTrack()
            binding.artistName.text = currentTitle.artist
            binding.trackTitle.text = currentTitle.trackName
        }
    }

    private fun updateBottomPlayerSeekbar(trackPosition: Long) {
        val withOfSeekbar =
            (trackPosition / getCurrentTrackDurationAsDouble()) * screenWidth

        val layoutParams = binding.bottomSeekbar.layoutParams
        layoutParams.width = withOfSeekbar.toInt()
        binding.bottomSeekbar.layoutParams = layoutParams
    }

    private fun getNotificationAction() {
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(_context: Context?, _intent: Intent?) {
                _context?.let { context ->
                    _intent?.let { intent ->
                        when (intent.extras?.getString("actionName") ?: "") {
                            NotificationActions.NEXT.actionName -> {
                                playingMusicService?.let { playingMusicService ->
                                    playingMusicService.playNextTrack()
                                    viewModel.onEvent(ActivityEvent.NotificationNextAction(playingMusicService.getCurrentPositionOfTrack()))
                                    setTrackDetailToBottomSeekbar()
                                }
                            }
                            NotificationActions.PREVIOUS.actionName -> {
                                playingMusicService?.let { playingMusicService ->
                                    playingMusicService.playPreviousTrack()
                                    viewModel.onEvent(ActivityEvent.NotificationPreviousAction(playingMusicService.getCurrentPositionOfTrack()))
                                    setTrackDetailToBottomSeekbar()
                                }
                            }

                            NotificationActions.CLOSE.actionName -> {
                                viewModel.onEvent(ActivityEvent.OnCloseButtonClick)
                                playingMusicService?.release()
                            }

                            NotificationActions.PLAY.actionName -> {
                                playingMusicService?.let { service ->
                                    val isTrackPlaying = service.isTrackPlaying()
                                    setTrackDetailToBottomSeekbar()
                                    viewModel.onEvent(ActivityEvent.NotificationPlayPauseAction(service.getCurrentPositionOfTrack(), isTrackPlaying))
                                    if (isTrackPlaying) {
                                        service.pauseTrack()
                                    } else {
                                        service.resumeTrack()
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                }
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
    private fun getCurrentTrackDurationAsDouble(): Double {
        return playingMusicService?.getCurrentTrack()?.duration?.toDouble() ?: 1.0
    }

    override fun onDestroy() {
        super.onDestroy()
        playingMusicService?.release()
    }
}