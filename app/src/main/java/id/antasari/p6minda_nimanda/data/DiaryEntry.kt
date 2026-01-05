// Package sudah disesuaikan dengan ID Anda
package id.antasari.p6minda_nimanda.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val mood: String,
    val timestamp: Long
)