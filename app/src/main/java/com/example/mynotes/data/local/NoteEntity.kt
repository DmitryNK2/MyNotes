// NoteEntity
package com.example.mynotes.data.local  // ← проверьте пакет!

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = SubTopicEntity::class,
        parentColumns = ["id"],
        childColumns = ["subTopicId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,  // ← это поле обязательно
    val subTopicId: Long,
    val content: String,
    val tag: String? = null,
    val emojiReaction: String? = null,
    val isEdited: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)