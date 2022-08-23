package com.amirreza.musicplayer.features.feature_music.presentation.fragment_home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.databinding.ItemTrackBinding
import com.amirreza.musicplayer.features.feature_music.domain.entities.Album
import com.amirreza.musicplayer.features.feature_music.domain.entities.Artist
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import com.amirreza.musicplayer.features.feature_music.presentation.MusicHelper
import com.amirreza.musicplayer.features.feature_music.presentation.fragment_home.util.OnItemClickEvent
import com.bumptech.glide.Glide


class ItemListAdapter<T>(
    val onItemClickEvent: OnItemClickEvent,
    val context: Context,
    private val itemList: List<T>
) : RecyclerView.Adapter<ItemListAdapter<T>.ItemHolder>() {

    inner class ItemHolder(private val binding: ItemTrackBinding) :RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: T) {

            var pic = ""
            var title = ""
            var caption = ""
            var duration:Long = 0

            if(item is Track){
                pic = item.albumArtPic
                title = item.trackName
                caption = item.artist
                duration = item.duration
            }else if(item is Album) {
                pic = item.tracks[0].albumArtPic
                title = item.albumName
                caption = "${item.artistName} (${item.tracks.size})"
                duration = item.tracks.sumOf { it.duration }
            }else if(item is Artist){
                pic = item.albums[0].tracks[0].albumArtPic
                title = item.name
                caption = "album count : ${item.albums.size}"
                duration = item.calculateDuration()
            }



            Glide.with(context)
                .load(pic)
                .centerCrop()
                .placeholder(R.drawable.ic_album_24)
                .into(binding.artcPicOfItem)

            binding.titleOfItem.text = title
            binding.trackDuration.text = MusicHelper.convertDurationOfMusicToNormalForm(duration)
            binding.caption.text = caption
            binding.item.setOnClickListener {
                onItemClickEvent.click(item)
            }
            binding.moreIcon.setOnClickListener {

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ItemHolder(ItemTrackBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}