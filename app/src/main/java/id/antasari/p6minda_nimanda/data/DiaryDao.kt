package id.antasari.p6minda_nimanda.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {

    /**
     * PERBAIKAN: Fungsi insert sekarang mengembalikan 'Long'.
     * Ini adalah ID dari baris yang baru saja dimasukkan,
     * yang akan memperbaiki error .toInt() di NewEntryScreen.kt
     */
    @Insert
    suspend fun insert(entry: DiaryEntry): Long // <-- PERUBAHAN DI SINI

    @Update
    suspend fun update(entry: DiaryEntry)

    @Delete
    suspend fun delete(entry: DiaryEntry)

    @Query("SELECT * FROM diary_entries ORDER BY timestamp DESC")
    suspend fun getAll(): List<DiaryEntry>

    @Query("SELECT * FROM diary_entries WHERE id = :entryId LIMIT 1")
    suspend fun getById(entryId: Int): DiaryEntry?

    // ✅ Tambahan baru untuk CalendarViewModel
    @Query("SELECT * FROM diary_entries ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<DiaryEntry>>
}