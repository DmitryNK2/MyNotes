// SubTopicEntity
package com.example.mynotes.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sub_topics",
    foreignKeys = [ForeignKey(
        entity = TopicEntity::class,
        parentColumns = ["id"],
        childColumns = ["topicId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SubTopicEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: Long,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)