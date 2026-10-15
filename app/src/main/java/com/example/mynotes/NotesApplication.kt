// NotesApplication
package com.example.mynotes

import android.app.Application
import com.example.mynotes.data.local.AppDatabase
import com.example.mynotes.data.repository.NotesRepository

class NotesApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        NotesRepository(
            database.topicDao(),
            database.subTopicDao(),
            database.noteDao()
        )
    }
}