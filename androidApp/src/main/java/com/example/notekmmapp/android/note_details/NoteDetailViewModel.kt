package com.example.notekmmapp.android.note_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notekmmapp.domain.note.Note
import com.example.notekmmapp.domain.note.NoteDataSource
import com.example.notekmmapp.domain.time.DateTimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteDataSource: NoteDataSource,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val noteTitle = savedStateHandle.getStateFlow("noteTitle", "")
    private val noteContent = savedStateHandle.getStateFlow("noteContent", "")
    private val isNoteTitleTextFocused = savedStateHandle.getStateFlow("isNoteTitleTextFocused", false)
    private val isNoteContentTextFocused = savedStateHandle.getStateFlow("isNoteContentTextFocused", false)
    private val noteColor = savedStateHandle.getStateFlow("noteColor",
        Note.generateRandomColor()
    )

    val state = combine(noteTitle, noteContent, isNoteTitleTextFocused, isNoteContentTextFocused, noteColor) {
            noteTitle, noteContent, isNoteTitleFocused, isNoteContentFocused, noteColor ->
        NoteDetailsState(
            noteTitle = noteTitle,
            noteContent = noteContent,
            isNoteTitleHintVisible = noteTitle.isEmpty() && !isNoteTitleFocused,
            isNoteContentHintVisible  = noteContent.isEmpty() && ! isNoteContentFocused,
            noteColor = noteColor
        )
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteDetailsState())


    private val _hasNoteBeenSaved = MutableStateFlow(false)
    val hasNoteBeenSaved = _hasNoteBeenSaved.asStateFlow()

    private var existingNoteId: Long? = null

    init {
        savedStateHandle.get<Long>("noteId")?.let { existingNoteId ->
            if (existingNoteId == -1L) return@let
            this.existingNoteId = existingNoteId
            viewModelScope.launch {
                noteDataSource.getNoteById(existingNoteId)?.let { note ->
                    savedStateHandle["noteTitle"] = note.title
                    savedStateHandle["noteContent"] = note.content
                    savedStateHandle["noteColor"] = note.colorHex
                }
            }
        }
    }

    fun onNoteTitleChanged(noteTitle: String) {
        savedStateHandle["noteTitle"] = noteTitle
    }

    fun onNoteContentChanged(noteContent: String) {
        savedStateHandle["noteContent"] = noteContent
    }

    fun onNoteColorChanged(noteColor: Long) {
        savedStateHandle["noteColor"] = noteColor
    }

    fun onNoteTitleTextFocusChanged(isFocused: Boolean) {
        savedStateHandle["isNoteTitleTextFocused"] = isFocused
    }

    fun onNoteContentTextFocusChanged(isFocused: Boolean) {
        savedStateHandle["isNoteContentTextFocused"] = isFocused
    }

    fun saveNote() {
        viewModelScope.launch {
            noteDataSource.insertNote(
                Note(
                    id = existingNoteId,
                    title = noteTitle.value,
                    content = noteContent.value,
                    colorHex = noteColor.value,
                    created = DateTimeUtil.now(),
                )
            )
            _hasNoteBeenSaved.value = true
        }
    }
}