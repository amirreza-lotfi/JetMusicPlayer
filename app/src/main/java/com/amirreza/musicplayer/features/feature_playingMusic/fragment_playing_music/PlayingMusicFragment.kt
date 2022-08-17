package com.amirreza.musicplayer.features.feature_playingMusic.fragment_playing_music

import android.content.*
import android.content.Context.BIND_AUTO_CREATE
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.databinding.FragmentPlayingMusicBinding
import com.amirreza.musicplayer.features.feature_music.presentation.MusicHelper
import com.amirreza.musicplayer.features.feature_playingMusic.OnSeekbarEvent
import com.amirreza.musicplayer.features.feature_playingMusic.PlayingFragmentEvent
import com.amirreza.musicplayer.features.feature_playingMusic.SeekBar
import com.amirreza.musicplayer.features.feature_playingMusic.services.PlayingMusicService
import com.amirreza.musicplayer.general.EXTRA_TRACK_LIST
import com.amirreza.musicplayer.general.JetFragment
import com.amirreza.musicplayer.general.NotificationActions
import com.amirreza.musicplayer.general.NotificationConst.NOTIFICATION_ACTION_BROADCAST
import com.bumptech.glide.Glide
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import kotlin.collections.ArrayList


class PlayingMusicFragment : JetFragment() {
    lateinit var binding: FragmentPlayingMusicBinding
    private val viewModel: PlayingMusicViewModel by inject() { parametersOf(this.arguments) }
    private var playingMusicService: PlayingMusicService? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayingMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.trackList.value?.let { tracks ->
            activity?.let { activity ->
                val intent = Intent(requireContext(), PlayingMusicService::class.java)
                intent.putParcelableArrayListExtra(EXTRA_TRACK_LIST, tracks as ArrayList)
                activity.startService(intent)
                activity.registerReceiver(object : BroadcastReceiver() {
                    override fun onReceive(_context: Context?, _intent: Intent?) {
                        _context?.let { context ->
                            _intent?.let { intent ->
                                when (intent.extras?.getString("actionName") ?: "") {
                                    NotificationActions.NEXT.actionName -> {
                                        playingMusicService?.playNextTrack()
                                    }
                                    NotificationActions.PREVIOUS.actionName -> {
                                        playingMusicService?.playPreviousTrack()
                                    }
                                    NotificationActions.CLOSE.actionName -> {
                                        //todo
                                    }
                                    NotificationActions.PLAY_PAUSE.actionName -> {
                                        playingMusicService?.let { service ->
                                            if (service.isTrackPlaying())
                                                service.pauseTrack()
                                            else {
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
                activity.bindService(intent, object : ServiceConnection {
                    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                        playingMusicService = (p1 as PlayingMusicService.PlayingMusicBinder).getService()

                        viewModel.onUiEvent(PlayingFragmentEvent.SetIsTrackPlayingLiveData(true))
                        viewModel.startTrackPosition()

                        playingMusicService?.let{ playingMusicService ->

                            val slider = binding.slider

                            slider.post {
                                val seekbar = SeekBar(binding.slider,viewModel.currentTrack.value?.duration ?:0,playingMusicService.getCurrentPositionOfTrack() ?: 0L)

                                seekbar.setOnSeekbarTouchedListener(object : OnSeekbarEvent {
                                    override fun onCurrentPositionChanged(touchedPosition: Long) {
                                        playingMusicService.seekTrackTo(touchedPosition)
                                        viewModel.onUiEvent(PlayingFragmentEvent.OnSeekBarTouched(touchedPosition))
                                    }

                                    override fun onTrackFinished() {
                                        slider.updateSeekbarByNewValue(0)
                                        viewModel.onUiEvent(PlayingFragmentEvent.OnTrackFinished(playingMusicService.playNextTrack()))
                                    }
                                })


                                viewModel.trackPosition.observe(viewLifecycleOwner){
                                    slider.updateSeekbarByNewValue(it)
                                    setCurrentPositionOfTrackText(it.toLong())
                                }
                            }

                            binding.pausePlayBtn.setOnClickListener {
                                viewModel.onUiEvent(PlayingFragmentEvent.PausePlayButtonClicked)
                            }

                            binding.playNextTrack.setOnClickListener {
                                val newTrack = playingMusicService.playNextTrack()
                                viewModel.onUiEvent(PlayingFragmentEvent.OnNextTrackClicked(newTrack))
                            }

                            binding.playPreviousTrack.setOnClickListener {
                                val newTrack = playingMusicService.playPreviousTrack()
                                viewModel.onUiEvent(PlayingFragmentEvent.OnPreviousTrackClicked(newTrack))
                            }
                        }

                        binding.playNextTrack.visibility = if(!playingMusicService!!.hasNextTrack()) View.INVISIBLE else View.VISIBLE
                        binding.playPreviousTrack.visibility = if(!playingMusicService!!.hasPreviousTrack()) View.INVISIBLE else View.VISIBLE

                    }

                    override fun onServiceDisconnected(p0: ComponentName?) {
                        playingMusicService = null
                    }

                }, BIND_AUTO_CREATE)
            }
        }

        viewModel.currentTrack.observe(viewLifecycleOwner) { current ->
            setTrackName(current.trackName)
            setTrackArtist(current.artist)
            setTrackBackgroundImage(current.albumArtPic)
            setTrackMainImage(current.albumArtPic)
            setCurrentPositionOfTrackText(playingMusicService?.getCurrentPositionOfTrack() ?: 0L)
            setTrackDurationText(current.duration)

            playingMusicService?.let { playingMusicService ->
                binding.playNextTrack.visibility = if(!playingMusicService.hasNextTrack()) View.INVISIBLE else View.VISIBLE
                binding.playPreviousTrack.visibility = if(!playingMusicService.hasPreviousTrack()) View.INVISIBLE else View.VISIBLE
            }

        }

        viewModel.isTrackPlaying.observe(viewLifecycleOwner){ isPlaying->
            setUpPlayingOrPauseButtonImageResource(isPlaying)
            playingMusicService?.let { playingService->
                if(isPlaying){
                    playingService.resumeTrack()
                }else{
                    playingService.pauseTrack()
                }
            }
        }

        viewModel.trackPosition.observe(viewLifecycleOwner){

        }
    }

    fun setUpPlayingOrPauseButtonImageResource(isTrackPlaying:Boolean){
        if(isTrackPlaying){
            binding.iconOfCenterActionPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24)
        }else{
            binding.iconOfCenterActionPlayOrPause.setImageResource(R.drawable.ic_play)
        }
    }


    private fun setTrackArtist(artist: String) {
        binding.songArtist.text = artist
    }

    private fun setTrackName(trackName: String) {
        binding.nameOfSong.text = trackName
        binding.songNameInMaterialCardView.text = trackName
    }

    private fun setTrackBackgroundImage(trackArtPath: String) {
        Glide.with(requireContext())
            .load(trackArtPath)
            .centerCrop()
            .placeholder(R.drawable.ic_album_24)
            .into(binding.backgroundTrackImage)
    }

    private fun setTrackMainImage(trackArtPath: String) {
        Glide.with(requireContext())
            .load(trackArtPath)
            .centerCrop()
            .placeholder(R.drawable.ic_album_24)
            .into(binding.trackMainImage)
    }

    private fun setCurrentPositionOfTrackText(pos: Long) {
        binding.currentPositionOfTrackByMinute.text =
            MusicHelper.convertDurationOfMusicToNormalForm(pos)
    }

    private fun setTrackDurationText(pos: Long) {
        binding.durationOfTrack.text = MusicHelper.convertDurationOfMusicToNormalForm(pos)
    }

    override fun onDestroy() {
        playingMusicService?.onDestroy()
        super.onDestroy()
    }

}