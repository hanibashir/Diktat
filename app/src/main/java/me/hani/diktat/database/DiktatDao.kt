package me.hani.diktat.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DiktatDao {

    //category
    @Insert
    suspend fun addCat(cat: Category)

    @Update
    suspend fun updateCat(cat: Category)

    @Delete
    suspend fun deleteCat(cat: Category)

    //get category list with words list
    @Transaction
    @Query("SELECT * FROM category_table ORDER BY id DESC")
    fun getCatsListWithWordsList(): LiveData<List<CategoryWithWords>>

    //words
    @Insert
    suspend fun addWord(word: Word)

    @Update
    suspend fun updateWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)

    @Query("SELECT * FROM word_table WHERE cat_id Like :cat_id")
    fun getCategoryWordList(cat_id: Int): LiveData<List<Word>>

    //delete words if the category deleted
    @Query("DELETE FROM word_table WHERE cat_id Like :cat_id")
    suspend fun deleteWordWithCat(cat_id: Int)

    //update word's is_correct column after test
    @Query("UPDATE word_table SET is_correct = :newValue WHERE id = :word_id")
    suspend fun updateCorrectWrong(newValue: Int, word_id: Int)

    @Query("SELECT * FROM word_table WHERE is_correct LIKE :isCorrect")
    fun getWordsListWithIsCorrect(isCorrect: Int): LiveData<List<Word>>

}