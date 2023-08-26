package com.example.notekmmapp.di

import com.example.notekmmapp.data.local.DatabaseDriverFactory
import com.example.notekmmapp.data.note.SqlDelightNoteDataSource
import com.example.notekmmapp.database.NoteDatabase
import com.example.notekmmapp.domain.note.NoteDataSource


class DatabaseModule {

    private val factory by lazy { DatabaseDriverFactory() }
    val noteDataSource: NoteDataSource by lazy {
        SqlDelightNoteDataSource(NoteDatabase(factory.createDriver()))
    }
}