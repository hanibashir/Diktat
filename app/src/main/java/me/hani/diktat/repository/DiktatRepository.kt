package me.hani.diktat.repository

import androidx.lifecycle.LiveData
import me.hani.diktat.database.Category
import me.hani.diktat.database.CategoryWithWords
import me.hani.diktat.database.Word
import me.hani.diktat.database.DiktatDao

class DiktatRepository(private val diktatDao: DiktatDao) {


    //category
    val getCatsListWithWordsList: LiveData<List<CategoryWithWords>> =
        diktatDao.getCatsListWithWordsList()

    suspend fun addCat(cat: Category) = diktatDao.addCat(cat)
    suspend fun deleteCat(cat: Category) = diktatDao.deleteCat(cat)
    suspend fun updateCat(cat: Category) = diktatDao.updateCat(cat)


    //words
    fun getCategoryWordList(cat_id: Int): LiveData<List<Word>> =
        diktatDao.getCategoryWordList(cat_id)

    suspend fun addWord(word: Word) = diktatDao.addWord(word)
    suspend fun deleteWord(word: Word) = diktatDao.deleteWord(word)
    suspend fun updateWord(word: Word) = diktatDao.updateWord(word)
    suspend fun deleteWordWithCat(cat_id: Int) = diktatDao.deleteWordWithCat(cat_id)
    suspend fun updateCorrectWrong(newValue: Int, word_id: Int) =
        diktatDao.updateCorrectWrong(newValue, word_id)

    fun getWordsListWithIsCorrect(isCorrect: Int) =
        diktatDao.getWordsListWithIsCorrect(isCorrect)


}