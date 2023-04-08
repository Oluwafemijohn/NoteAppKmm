package com.example.notekmmapp.android.note_details


data class NoteDetailsState(
    val noteTitle: String = "",
    val noteContent: String = "",
    val isNoteTitleHintVisible: Boolean = false,
    val isNoteContentHintVisible: Boolean = false,
    val noteColor: Long = 0xFFFFFF
)