package com.amirreza.musicplayer.di

import android.app.Application
import com.amirreza.musicplayer.features.feature_music.data.MusicRepositoryImpl
import com.amirreza.musicplayer.features.feature_music.domain.repository.MusicRepository
import com.amirreza.musicplayer.features.feature_music.presentation.home_screen.MusicViewModel
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
        }
        val modulesViewModel = module {
            viewModel {
                MusicViewModel(get())
            }
        }

        startKoin {
            androidContext(this@JetApplication)
            modules(listOf(generalModule,modulesViewModel))
        }
    }
}