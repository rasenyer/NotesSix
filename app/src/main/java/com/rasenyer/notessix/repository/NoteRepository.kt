package com.rasenyer.notessix.repository

import androidx.lifecycle.LiveData
import com.rasenyer.notessix.datasource.local.model.Note
import com.rasenyer.notessix.datasource.local.database.NoteDatabase
import javax.inject.Inject

class NoteRepository @Inject constructor(private val noteDatabase: NoteDatabase) {

    suspend fun insert(note: Note) = noteDatabase.noteDao().insert(note)

    suspend fun update(note: Note) = noteDatabase.noteDao().update(note)

    suspend fun delete(note: Note) = noteDatabase.noteDao().delete(note)

    suspend fun deleteAll() = noteDatabase.noteDao().deleteAll()

    fun searchByTitle(query: String?): LiveData<List<Note>> { return noteDatabase.noteDao().searchByTitle(query) }

    fun getAll() = noteDatabase.noteDao().getAll()

}