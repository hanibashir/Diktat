package me.hani.diktat.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "category_table")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
)

data class CategoryWithWords(

    @Embedded
    val cat: Category,
    @Relation(parentColumn = "id", entityColumn = "cat_id")
    val catWordsList: List<Word>
)