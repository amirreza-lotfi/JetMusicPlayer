package com.amirreza.musicplayer.features.feature_music.presentation.home_screen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.databinding.ItemTrackBinding
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_music.presentation.MusicHelper
import com.amirreza.musicplayer.features.feature_music.presentation.home_screen.util.OnTrackClickEvent
import com.bumptech.glide.Glide

class TracksAdapter(
    val onTrackClickEvent: OnTrackClickEvent,
    val context: Context,
    private val trackList: List<Track>
) : RecyclerView.Adapter<TracksAdapter.TrackHolder>() {

    inner class TrackHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(track: Track) {
            binding.track.setOnClickListener {
                onTrackClickEvent.click(track)
            }
            Glide.with(context)
                .load(track.albumArtPic)
                .centerCrop()
                .placeholder(R.drawable.ic_album_24)
                .into(binding.trackArtPic)
            binding.nameOfTrack.text = track.trackName
            binding.moreIcon.setOnClickListener {

            }
            binding.trackDuration.text =
                MusicHelper.convertDurationOfMusicToNormalForm(track.duration)
            binding.artistOfTrack.text = track.artist
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TrackHolder(ItemTrackBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.onBind(trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }
}