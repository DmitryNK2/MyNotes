// TopicEntity
package com.example.mynotes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isFolder: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)