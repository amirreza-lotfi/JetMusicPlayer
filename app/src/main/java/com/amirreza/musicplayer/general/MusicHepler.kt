package com.amirreza.musicplayer.features.feature_music.presentation

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.amirreza.musicplayer.R
import com.amirreza.musicplayer.features.feature_music.domain.entities.Album
import com.amirreza.musicplayer.features.feature_music.domain.entities.Artist
import com.amirreza.musicplayer.features.feature_music.domain.entities.Track
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class MusicHelper {
    companion object{
        fun getTracks(context: Context): List<Track> {

            val musicList = mutableListOf<Track>()
            val collection = if(isVersionQ()) MediaStore.Audio.Media.BUCKET_DISPLAY_NAME else MediaStore.Audio.Media.DATA

            val projection = arrayOf(
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,  // error from android side, it works < 29
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ALBUM,
                collection,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.DATA
            )
            val selection:String = MediaStore.Audio.Media.IS_MUSIC + " = 1"
            val sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER

            val musicCursor = context!!
                .contentResolver
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,sortOrder)

            musicCursor?.let {
                val artistInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val yearInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val trackInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
                val titleInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val displayNameInd =
                    musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val durationInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val albumIdInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val albumInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val relativePathInd = musicCursor.getColumnIndexOrThrow(collection)
                val idInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val dateModifiedInd =
                    musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
                val contentUriInd = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

                while (musicCursor.moveToNext()) {
                    val artist = musicCursor.getString(artistInd)
                    val title = musicCursor.getString(titleInd)
                    val displayName = musicCursor.getString(displayNameInd)
                    val album = musicCursor.getString(albumInd)
                    var relativePath = musicCursor.getString(relativePathInd)
                    val absolutePath = musicCursor.getString(contentUriInd)
                    if (isVersionQ()) relativePath += "/" else if (relativePath != null) {
                        val check = File(relativePath).parentFile
                        if (check != null) {
                            relativePath = check.name + "/"
                        }
                    } else {
                        relativePath = "/"
                    }
                    val year = musicCursor.getInt(yearInd)
                    val track = musicCursor.getInt(trackInd)
                    val startFrom = 0
                    val dateAdded = musicCursor.getInt(dateModifiedInd)
                    val id = musicCursor.getLong(idInd)
                    val duration = musicCursor.getLong(durationInd)
                    val albumId = musicCursor.getLong(albumIdInd)
                    musicList.add(
                        Track(
                            artist = artist,
                            title,
                            displayName,
                            album,
                            relativePath,
                            absolutePath,
                            year =year,
                            track = track,
                            startFrom = startFrom,
                            dateAdded = dateAdded,
                            id = id,
                            duration = duration,
                            albumId = albumId,
                            albumArtPic =
                            ContentUris
                                .withAppendedId(
                                    Uri.parse(context.resources.getString(R.string.album_art_dir))
                                    ,albumId
                                )
                                .toString()
                        )
                    )
                }

                if (!musicCursor.isClosed) musicCursor.close()

                return musicList
            }

            return emptyList()
        }
        fun extractAlbums(trackList:List<Track>): List<Album> {
            if(trackList.isEmpty())
                return emptyList()

            val map = hashMapOf<Long, Album>() //album name and album obj

            for(track: Track in trackList){
                if(map.containsKey(track.albumId)){
                    val album = map[track.albumId]
                    album?.let {
                        album.duration+= track.duration
                        album.tracks.add(track)
                    }
                }
                else{
                    val album = Album(
                        artistName = track.artist,
                        albumName = track.albumName,
                        year = track.year.toString(),
                        duration = track.duration,
                        tracks = mutableListOf(track)
                    )
                    map[track.albumId] = album
                }
            }

            val albums = map.values.toMutableList()
            return albums.sortedBy {
                it.albumName
            }
        }
        fun extractArtists(albumList:List<Album>): List<Artist> {
            val map = hashMapOf<String, Artist>()

            for(album: Album in albumList){
                if(map.containsKey(album.artistName)){
                    val artist = map[album.artistName]
                    artist?.let{
                        artist.albumCount+=1
                        artist.albums.add(album)
                        artist.songCount+= album.tracks.size
                    }
                }else{
                    val artist = Artist(
                        name = album.artistName,
                        albums = mutableListOf(album),
                        songCount = album.tracks.size,
                        albumCount = 1,
                    )
                    map[artist.name] = artist
                }
            }

            return map.values.toList()
        }
        private fun isVersionQ(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        }
        fun convertDurationOfMusicToNormalForm(duration: Long): String {
            val durationToSecond = TimeUnit.MILLISECONDS.toSeconds(duration)
            var minute = (durationToSecond/60).toString()
            var second = (durationToSecond%60).toString()
            if(minute.length<=1){
                minute = "0$minute"
            }
            if(second.length<=1){
                second = "0$second"
            }
            return "$minute:$second"
        }
        fun getBitmapOfTrack(context: Context, track: Track):Bitmap?{
            val uri = track.albumArtPic
            if (track.albumArtPic.isBlank())
                return null

            return try {
                val fileDescriptor = context.contentResolver.openFileDescriptor(Uri.parse(uri), "r")
                if (uri == null) return null
                val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor!!.fileDescriptor)
                fileDescriptor.close()
                bitmap
            } catch (e: IOException) {
                null
            }

        }
    }
}