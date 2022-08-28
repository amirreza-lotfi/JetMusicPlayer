package com.amirreza.musicplayer

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.navigation.findNavController
import com.amirreza.ActivityEvent
import com.amirreza.musicplayer.databinding.ActivityMainBinding
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_playingMusic.services.PlayerListener
import com.amirreza.musicplayer.features.feature_playingMusic.services.PlayingMusicService
import com.amirreza.musicplayer.general.NotificationConst.NOTIFICATION_ACTION_BROADCAST
import com.amirreza.musicplayer.general.getScreenWidth

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var playingMusicService: PlayingMusicService? = null
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

        bindService(intent, object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                playingMusicService = (p1 as PlayingMusicService.PlayingMusicBinder).getService()

                playingMusicService?.let { playingMusicService ->
                    Log.i("mainActivity", "serviceConnected")
                    viewModel.onEvent(
                        ActivityEvent.OnServiceAttached(
                            playingMusicService.getCurrentTrack(),
                            playingMusicService.getCurrentPositionOfTrack()
                        )
                    )
                    setTrackDetailToBottomSeekbar(playingMusicService.getCurrentTrack())

                    playingMusicService.onPlayingTrackListener(object : PlayerListener {
                        override fun onFinishTrack(nextTrack: Track) {
                            val isTrackPlaying = playingMusicService.isTrackPlaying()
                            val trackPosition = playingMusicService.getCurrentPositionOfTrack()

                            setTrackDetailToBottomSeekbar(nextTrack)
                            viewModel.onEvent(ActivityEvent.OnTrackFinished(nextTrack,isTrackPlaying,trackPosition))
                        }

                        override fun onMusicPlayerFinishPlayingAllMedia() {
                            TODO("Not yet implemented")
                        }

                        override fun isTrackPlaying(boolean: Boolean) {
                            val currentPositionOfTrack = playingMusicService.getCurrentPositionOfTrack()
                            viewModel.onEvent(ActivityEvent.SetIsTrackPlayingLiveData(boolean))
                            viewModel.onEvent(ActivityEvent.IsTrackPlayingEvent(boolean,currentPositionOfTrack))
                            updateBottomPlayerSeekbar(currentPositionOfTrack)
                        }

                        override fun onTrackPositionChanged(newPosition: Int) {

                        }
                    })

                    viewModel.observeToPositionOfTrack().observe(this@MainActivity){
                        val currentFragmentIsPlayingFragment = currentFragmentIsPlayingFragment()

                        if(currentFragmentIsPlayingFragment){
                            binding.bottomTrackPlayer.visibility = GONE
                        }else{
                            binding.bottomTrackPlayer.visibility = VISIBLE
                            updateBottomPlayerSeekbar(it)
                        }
                    }
                }
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                Log.i("mainActivity", "service DisConnected")
            }
        }, BIND_ABOVE_CLIENT)

        viewModel.isTrackPlaying.observe(this) { isPlaying ->
            setUpPlayingOrPauseButtonImageResource(isPlaying)
            if (isPlaying) {
                binding.bottomTrackPlayer.visibility = VISIBLE
            }
        }

        binding.playPauseBtn.setOnClickListener {
            val isTrackPlaying = playingMusicService?.isTrackPlaying() ?: false
            val trackPosition = playingMusicService?.getCurrentPositionOfTrack() ?: 0L

            if (isTrackPlaying)
                playingMusicService?.pauseTrack()
            else
                playingMusicService?.resumeTrack()

            viewModel.onEvent(ActivityEvent.PausePlayButtonClicked(isTrackPlaying,trackPosition))
        }

        binding.closeServiceBtn.setOnClickListener {
            binding.bottomTrackPlayer.visibility = View.GONE
            playingMusicService?.release()
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
    fun updateBottomPlayerSeekbar(trackPosition:Long){
        val withOfSeekbar = (trackPosition / viewModel.getCurrentTrackDurationAsDouble()) * screenWidth

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
    private fun currentFragmentIsPlayingFragment():Boolean{
        val destination = findNavController(R.id.fragmentContainerView).currentBackStackEntry?.destination?.displayName ?: "null"
        if (destination == "com.amirreza.musicplayer:id/playingMusicFragment")
            return true
        return false
    }

}