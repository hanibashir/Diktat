package me.hani.diktat.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@kotlinx.parcelize.Parcelize
@Entity(tableName = "word_table")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val artikel: String,
    val word: String,
    val plural: String,
    val cat_id: Int,
    val is_correct: Int? = null
): Parcelable



