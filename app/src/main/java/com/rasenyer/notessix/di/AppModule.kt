package com.rasenyer.notessix.di

import android.app.Application
import com.rasenyer.notessix.datasource.local.database.NoteDatabase
import com.rasenyer.notessix.datasource.local.storage.UIModeDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Singleton

@InstallIn(ActivityComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun providePreferenceManager(application: Application): UIModeDataStore {
        return UIModeDataStore(application.applicationContext)
    }

    @Singleton
    @Provides
    fun provideNoteDatabase(application: Application): NoteDatabase {
        return NoteDatabase.invoke(application.applicationContext)
    }

}