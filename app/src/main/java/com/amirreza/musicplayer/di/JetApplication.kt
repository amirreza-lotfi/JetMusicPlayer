package com.amirreza.musicplayer.di

import android.app.Application
import android.os.Bundle
import com.amirreza.musicplayer.features.feature_music.data.MusicRepositoryImpl
import com.amirreza.musicplayer.features.feature_music.domain.repository.MusicRepository
import com.amirreza.musicplayer.features.feature_music.presentation.feature_albums.AlbumsViewModel
import com.amirreza.musicplayer.features.feature_playingMusic.fragment_playing_music.PlayingMusicViewModel
import com.amirreza.musicplayer.features.feature_music.presentation.home_screen.MusicViewModel
import com.amirreza.musicplayer.features.feature_music.presentation.tracks_screen.TrackViewModel
import com.google.android.exoplayer2.ExoPlayer
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class JetApplication:Application() {
    override fun onCreate() {
        super.onCreate()

        val generalModule = module {
            single<MusicRepository> {
                MusicRepositoryImpl(this@JetApplication)
            }
            single{
                ExoPlayer.Builder(this@JetApplication).build()
            }
        }


        val modulesViewModel = module {
            viewModel {
                MusicViewModel(get())
            }
            viewModel { (bundle:Bundle)->
                PlayingMusicViewModel(bundle)
            }
            viewModel { (bundle:Bundle)->
                TrackViewModel(bundle)
            }
            viewModel { (bundle:Bundle)->
                AlbumsViewModel(bundle)
            }
        }

        startKoin {
            androidContext(this@JetApplication)
            modules(listOf(generalModule,modulesViewModel))
        }
    }
}