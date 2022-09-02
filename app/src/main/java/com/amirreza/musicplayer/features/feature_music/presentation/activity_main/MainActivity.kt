package com.amirreza.musicplayer.features.feature_music.presentation.activity_main

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.navigation.*
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.databinding.ActivityMainBinding
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_playingMusic.services.PlayerListener
import com.amirreza.musicplayer.features.feature_playingMusic.services.PlayingMusicService
import com.amirreza.musicplayer.general.NotificationActions
import com.amirreza.musicplayer.general.NotificationConst.NOTIFICATION_ACTION_BROADCAST
import com.amirreza.musicplayer.general.getScreenWidth
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    companion object {
        var playingMusicService: PlayingMusicService? = null
    }

    lateinit var binding: ActivityMainBinding
    val mainViewModel:MainViewModel by viewModel()
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
        Log.i("mainActivity14401", "onStart")
        mainViewModel.showingLandingFragment.observe(this) { mustShow ->
            if (!mustShow) {
                Log.i("mainActivityMain", "mustShow:$mustShow  navigateToHome")
                val navController = findNavController(R.id.fragmentContainerView)
                navController.popBackStack()
                navController.navigate("homeFragment")
            }
        }

        mainViewModel.bottomPlayingMustShow.observe(this) { mustShow ->
            binding.bottomTrackPlayer.visibility = if (mustShow) VISIBLE else GONE
        }

        observeToChangingFragment { destination ->

            if (destinationIsPlayingFragment(destination)) {
                mainViewModel.onEvent(ActivityEvent.FragmentChangedToPlayingFragment)
            } else {
                if (playingMusicService == null) {
                    mainViewModel.onEvent(ActivityEvent.PlayingServiceIsNotAvailable)
                } else {
                    mainViewModel.onEvent(
                        ActivityEvent.PlayingServiceIsAlive(
                            playingMusicService!!.getCurrentTrack(),
                            playingMusicService!!.getCurrentPositionOfTrack(),
                            playingMusicService!!.isTrackPlaying()
                        )
                    )
                    setTrackDetailToBottomSeekbar()
                    mainViewModel.observeToPositionOfTrack().observe(this@MainActivity) {
                        updateBottomPlayerSeekbar(it)
                    }
                }

                playingMusicService?.onPlayingTrackListener(object : PlayerListener {
                    override fun onFinishTrack(nextTrack: Track) {
                        playingMusicService?.let { service ->
                            setTrackDetailToBottomSeekbar()
                            val isTrackPlaying = service.isTrackPlaying()
                            val trackPosition = service.getCurrentPositionOfTrack()
                            mainViewModel.onEvent(
                                ActivityEvent.OnTrackFinished(
                                    isTrackPlaying,
                                    trackPosition
                                )
                            )
                        }
                    }

                    override fun onMusicPlayerFinishPlayingAllMedia() {
                        mainViewModel.onEvent(ActivityEvent.AllTracksFinished)
                    }

                    override fun isTrackPlaying(boolean: Boolean) {}
                    override fun onTrackPositionChanged(newPosition: Long) {}
                })
            }
        }

        mainViewModel.isTrackPlaying.observe(this) {
            setUpPlayingOrPauseButtonImageResource(it)
        }

        binding.playPauseBtn.setOnClickListener {
            val isTrackPlaying = playingMusicService?.isTrackPlaying() ?: false
            val trackPosition = playingMusicService?.getCurrentPositionOfTrack() ?: 0L

            mainViewModel.onEvent(ActivityEvent.PausePlayButtonClicked(isTrackPlaying, trackPosition))

            if (isTrackPlaying) {
                playingMusicService?.pauseTrack()
            } else {
                playingMusicService?.resumeTrack()
            }
        }

        binding.closeServiceBtn.setOnClickListener {
            mainViewModel.onEvent(ActivityEvent.OnCloseButtonClick)
            playingMusicService?.release()
            playingMusicService = null
        }

        binding.bottomTrackPlayer.setOnClickListener {
            findNavController(R.id.fragmentContainerView).navigate("playingFragmentRoute")
        }

    }

    override fun onResume() {
        super.onResume()
        Log.i("mainActivity14401", "onResume")

    }

    override fun onStop() {
        super.onStop()
        Log.i("mainActivity14401", "onStop")
    }

    override fun onPause() {
        super.onPause()
        Log.i("mainActivity14401", "onPause")

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
                                    mainViewModel.onEvent(
                                        ActivityEvent.NotificationNextAction(
                                            playingMusicService.getCurrentPositionOfTrack()
                                        )
                                    )
                                    setTrackDetailToBottomSeekbar()
                                }
                            }
                            NotificationActions.PREVIOUS.actionName -> {
                                playingMusicService?.let { playingMusicService ->
                                    playingMusicService.playPreviousTrack()
                                    mainViewModel.onEvent(
                                        ActivityEvent.NotificationPreviousAction(
                                            playingMusicService.getCurrentPositionOfTrack()
                                        )
                                    )
                                    setTrackDetailToBottomSeekbar()
                                }
                            }
                            NotificationActions.CLOSE.actionName -> {
                                mainViewModel.onEvent(ActivityEvent.OnCloseButtonClick)
                                playingMusicService?.release()
                                playingMusicService = null
                            }
                            NotificationActions.PLAY_PAUSE.actionName -> {
                                playingMusicService?.let { service ->
                                    val isTrackPlaying = service.isTrackPlaying()
                                    setTrackDetailToBottomSeekbar()
                                    mainViewModel.onEvent(
                                        ActivityEvent.NotificationPlayPauseAction(
                                            service.getCurrentPositionOfTrack(),
                                            isTrackPlaying
                                        )
                                    )
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
        Log.i("mainActivity14401", "onDestroy")
        playingMusicService?.release()
    }
}