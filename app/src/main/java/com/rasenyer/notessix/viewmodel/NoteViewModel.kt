package com.rasenyer.notessix.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rasenyer.notessix.datasource.local.model.Note
import com.rasenyer.notessix.datasource.local.storage.UIModeDataStore
import com.rasenyer.notessix.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    application: Application,
    private val noteRepository: NoteRepository
) : AndroidViewModel(application) {

    fun insert(note: Note) = viewModelScope.launch {
        noteRepository.insert(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        noteRepository.update(note)
    }

    fun delete(note: Note) = viewModelScope.launch {
        noteRepository.delete(note)
    }

    fun deleteAll() = viewModelScope.launch {
        noteRepository.deleteAll()
    }

    fun searchByTitle(query: String?): LiveData<List<Note>> {
        return noteRepository.searchByTitle(query)
    }

    fun getAll() = noteRepository.getAll()

    private val uiModeDataStore = UIModeDataStore(application)

    val getUIMode = uiModeDataStore.getUIMode

    fun saveUIModeToDataStore(isNightMode: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            uiModeDataStore.saveUIModeToDataStore(isNightMode)
        }
    }

}