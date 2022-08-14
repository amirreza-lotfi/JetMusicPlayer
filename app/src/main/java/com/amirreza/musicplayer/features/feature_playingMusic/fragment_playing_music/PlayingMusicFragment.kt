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
import com.amirreza.musicplayer.features.feature_playingMusic.OnSeekbarEvent
import com.amirreza.musicplayer.features.feature_playingMusic.services.PlayingMusicService
import com.amirreza.musicplayer.general.EXTRA_TRACK_LIST
import com.amirreza.musicplayer.general.JetFragment
import com.amirreza.musicplayer.general.NotificationActions
import com.amirreza.musicplayer.general.NotificationConst.NOTIFICATION_ACTION_BROADCAST
import com.bumptech.glide.Glide
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class PlayingMusicFragment : JetFragment() {
    lateinit var binding: FragmentPlayingMusicBinding
    private val viewModel: PlayingMusicViewModel by inject(){ parametersOf(this.arguments)}
    private var playingMusicService:PlayingMusicService? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayingMusicBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentTrack.observe(viewLifecycleOwner){
            setTrackName(it.trackName)
            setTrackArtist(it.artist)
            setTrackBackgroundImage(it.albumArtPic)
            setTrackMainImage(it.albumArtPic)
        }

        viewModel.trackList.value?.let { tracks->
            activity?.let { activity->
                val intent = Intent(requireContext(),PlayingMusicService::class.java)
                intent.putParcelableArrayListExtra(EXTRA_TRACK_LIST,tracks as ArrayList)
                activity.startService(intent)
                activity.registerReceiver(object: BroadcastReceiver(){
                    override fun onReceive(_context: Context?, _intent: Intent?) {
                        _context?.let{ context->
                            _intent?.let {  intent->
                                when(intent.extras?.getString("actionName") ?: ""){
                                    NotificationActions.NEXT.actionName->{
                                        playingMusicService?.playNextTrack()
                                    }
                                    NotificationActions.PREVIOUS.actionName->{
                                        playingMusicService?.playPreviousTrack()
                                    }
                                    NotificationActions.CLOSE.actionName->{
                                        //todo
                                    }
                                    NotificationActions.PLAY_PAUSE.actionName->{
                                        playingMusicService?.let { service->
                                            if(service.isTrackPlaying())
                                                service.pauseTrack()
                                            else{
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
                activity.bindService(intent,object : ServiceConnection{
                    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                        playingMusicService = (p1 as PlayingMusicService.PlayingMusicBinder).getService()
                    }

                    override fun onServiceDisconnected(p0: ComponentName?) {
                        playingMusicService = null
                    }

                },BIND_AUTO_CREATE)
            }
        }



        val slider = binding.slider
        slider.setCurrentValue(10000)
        slider.setMaxValue(10000000)

        slider.setOnSeekbarTouchedListener(object : OnSeekbarEvent{
            override fun onCurrentPositionChanged(touchedPosition: Long) {
                TODO("Not yet implemented")
            }
        })

    }






    private fun setTrackArtist(artist: String) {
        binding.songArtist.text = artist
    }
    private fun setTrackName(trackName:String) {
        binding.nameOfSong.text = trackName
        binding.songNameInMaterialCardView.text = trackName
    }
    private fun setTrackBackgroundImage(trackArtPath:String){
        Glide.with(requireContext())
            .load(trackArtPath)
            .centerCrop()
            .placeholder(R.drawable.ic_album_24)
            .into(binding.backgroundTrackImage)
    }
    private fun setTrackMainImage(trackArtPath:String){
        Glide.with(requireContext())
            .load(trackArtPath)
            .centerCrop()
            .placeholder(R.drawable.ic_album_24)
            .into(binding.trackMainImage)
    }

    override fun onDestroy() {
        playingMusicService?.onDestroy()
        super.onDestroy()
    }

}