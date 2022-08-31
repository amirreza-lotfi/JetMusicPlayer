package com.amirreza.musicplayer.features.feature_playingMusic.fragment_playing_music

import android.content.*
import android.content.Context.BIND_AUTO_CREATE
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.databinding.FragmentPlayingMusicBinding
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_music.presentation.MusicHelper
import com.amirreza.musicplayer.features.feature_music.presentation.activity_main.MainActivity
import com.amirreza.musicplayer.features.feature_music.presentation.activity_main.MainActivity.Companion.playingMusicService
import com.amirreza.musicplayer.features.feature_playingMusic.JetSeekBar
import com.amirreza.musicplayer.features.feature_playingMusic.OnSeekbarEvent
import com.amirreza.musicplayer.features.feature_playingMusic.PlayingFragmentEvent
import com.amirreza.musicplayer.features.feature_playingMusic.services.PlayerListener
import com.amirreza.musicplayer.features.feature_playingMusic.services.PlayingMusicService
import com.amirreza.musicplayer.general.*
import com.amirreza.musicplayer.general.NotificationConst.NOTIFICATION_ACTION_BROADCAST
import com.bumptech.glide.Glide
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class PlayingMusicFragment : JetFragment() {
    lateinit var binding: FragmentPlayingMusicBinding
    private val viewModel: PlayingMusicViewModel by inject()
    lateinit var slider: JetSeekBar
    lateinit var intentToPlayingService: Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayingMusicBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        intentToPlayingService = Intent(requireContext(), PlayingMusicService::class.java)

        if (playingMusicService == null) {
            val tracks = this.arguments?.getParcelableArrayList<Track>(EXTRA_TRACK_LIST) ?: arrayListOf()
            viewModel.indexOfSelectedItem = this.arguments?.getInt("IndexOfClickedTrack") ?: 0

            viewModel._currentTrack.value = tracks[ viewModel.indexOfSelectedItem]
            startPlayingTrackService(intentToPlayingService, tracks)
        }else{
            viewModel._currentTrack.value = playingMusicService!!.getCurrentTrack()
            viewModel.indexOfSelectedItem = playingMusicService!!.getCurrentTrackIndex()
        }
        getNotificationActions()
        bindToPlayingMusicService(intentToPlayingService)



        slider = binding.slider
        val widthOfSeekbar =
            ((getScreenWidth(requireActivity())) - convertDpToPixel(52f, requireContext())).toInt()

        slider.setValues(
            viewModel.currentTrack.value?.duration ?: 0,
            viewModel.trackPosition.value ?: 0L,
            widthOfSeekbar
        )

        viewModel.currentTrack.observe(viewLifecycleOwner) { current ->
            playingMusicService?.let { playingMusicService ->
                binding.playNextTrack.visibility =
                    if (!playingMusicService.hasNextTrack()) View.INVISIBLE else View.VISIBLE
                binding.playPreviousTrack.visibility =
                    if (!playingMusicService.hasPreviousTrack()) View.INVISIBLE else View.VISIBLE
            }

            binding.slider.setValues(
                viewModel.currentTrack.value?.duration ?: 0,
                0L,
                widthOfSeekbar
            )
            binding.slider.setOnSeekbarTouchedListener(object : OnSeekbarEvent {
                override fun onCurrentPositionChanged(touchedPosition: Long) {
                    playingMusicService?.seekTrackTo(touchedPosition)
                    viewModel.onUiEvent(PlayingFragmentEvent.OnSeekBarTouched(touchedPosition))
                }
            })


            setTrackName(current.trackName)
            setTrackArtist(current.artist)
            setTrackBackgroundImage(current.albumArtPic)
            setTrackMainImage(current.albumArtPic)
            setCurrentPositionText(playingMusicService?.getCurrentPositionOfTrack() ?: 0L)
            setTrackDurationText(current.duration)
        }

        viewModel.isTrackPlaying.observe(viewLifecycleOwner) { isPlaying ->
            setUpPlayingOrPauseButtonImageResource(isPlaying)
        }

        viewModel.trackPosition.observe(viewLifecycleOwner) {
            binding.slider.updateSeekbarByNewTrackPosition(it)
            setCurrentPositionText(it.toLong())
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpPlayingOrPauseButtonImageResource(isTrackPlaying: Boolean) {
        if (isTrackPlaying) {
            binding.iconOfCenterActionPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            binding.iconOfCenterActionPlayOrPause.setImageResource(R.drawable.ic_play)
        }
    }

    private fun startPlayingTrackService(intent: Intent, arrayList: ArrayList<Track>) {
        intent.putParcelableArrayListExtra(EXTRA_TRACK_LIST, arrayList)
        intent.putExtra(EXTRA_TRACK_CLICKED_POSITION, viewModel.indexOfSelectedItem)
        activity?.startService(intent)
    }

    private fun bindToPlayingMusicService(intent: Intent) {
        activity?.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            playingMusicService = (p1 as PlayingMusicService.PlayingMusicBinder).getService()
            viewModel.startTimer(playingMusicService?.getCurrentPositionOfTrack() ?: 0L)

            playingMusicService?.let { playingMusicService ->


                binding.pausePlayBtn.setOnClickListener {
                    viewModel.onUiEvent(PlayingFragmentEvent.PausePlayButtonClicked)
                    if (viewModel.isTrackPlaying.value == true) {
                        playingMusicService.pauseTrack()
                    } else {
                        playingMusicService.resumeTrack()
                    }
                }

                binding.playNextTrack.setOnClickListener {
                    val newTrack = playingMusicService.playNextTrack()
                    viewModel.onUiEvent(PlayingFragmentEvent.OnNextTrackClicked(newTrack))
                }

                binding.playPreviousTrack.setOnClickListener {
                    val newTrack = playingMusicService.playPreviousTrack()
                    viewModel.onUiEvent(
                        PlayingFragmentEvent.OnPreviousTrackClicked(
                            newTrack
                        )
                    )
                }

                playingMusicService.onPlayingTrackListener(object : PlayerListener {
                    override fun onFinishTrack(nextTrack: Track) {
                        binding.slider.updateSeekbarByNewTrackPosition(0L)
                        Log.i("fragmentPLayingggg", "set Value0 In track Finished: Done")
                        viewModel.onUiEvent(
                            PlayingFragmentEvent.OnTrackFinished(
                                nextTrack
                            )
                        )
                    }

                    override fun onMusicPlayerFinishPlayingAllMedia() {
                        binding.slider.updateSeekbarByNewTrackPosition(0L)
                    }

                    override fun isTrackPlaying(boolean: Boolean) {
                        viewModel.onUiEvent(
                            PlayingFragmentEvent.SetIsTrackPlayingLiveData(
                                boolean,
                                playingMusicService.getCurrentPositionOfTrack()
                            )
                        )
                    }

                    override fun onTrackPositionChanged(newPosition: Long) {
                    }
                })
            }

            binding.playNextTrack.visibility =
                if (!playingMusicService!!.hasNextTrack()) View.INVISIBLE else View.VISIBLE
            binding.playPreviousTrack.visibility =
                if (!playingMusicService!!.hasPreviousTrack()) View.INVISIBLE else View.VISIBLE

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            playingMusicService = null
        }
    }

    private fun getNotificationActions() {
        activity?.registerReceiver(broadcastReceiver, IntentFilter(NOTIFICATION_ACTION_BROADCAST))
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(_context: Context?, _intent: Intent?) {
            _context?.let { context ->
                _intent?.let { intent ->
                    when (intent.extras?.getString("actionName") ?: "") {
                        NotificationActions.NEXT.actionName -> {
                            playingMusicService?.let { playingMusicService ->
                                val newTrack = playingMusicService.playNextTrack()
                                viewModel.onUiEvent(
                                    PlayingFragmentEvent.OnNextTrackClicked(
                                        newTrack
                                    )
                                )
                            }
                        }
                        NotificationActions.PREVIOUS.actionName -> {
                            playingMusicService?.let { playingMusicService ->
                                val newTrack = playingMusicService.playPreviousTrack()
                                viewModel.onUiEvent(
                                    PlayingFragmentEvent.OnNextTrackClicked(
                                        newTrack
                                    )
                                )
                            }
                        }
                        NotificationActions.CLOSE.actionName -> {
                            playingMusicService?.onDestroy()
                            //todo
                        }
                        NotificationActions.PLAY.actionName -> {
                            playingMusicService?.let { service ->
                                if (service.isTrackPlaying()) {
                                    viewModel.onUiEvent(PlayingFragmentEvent.PausePlayButtonClicked)
                                    service.pauseTrack()
                                } else {
                                    viewModel.onUiEvent(PlayingFragmentEvent.PausePlayButtonClicked)
                                    service.resumeTrack()
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
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

    private fun setCurrentPositionText(pos: Long) {
        binding.currentPositionOfTrackByMinute.text =
            MusicHelper.convertDurationOfMusicToNormalForm(pos)
    }

    private fun setTrackDurationText(pos: Long) {
        binding.durationOfTrack.text = MusicHelper.convertDurationOfMusicToNormalForm(pos)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(broadcastReceiver)

    }
}