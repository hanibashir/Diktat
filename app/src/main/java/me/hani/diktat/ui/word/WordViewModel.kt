package me.hani.diktat.ui.word

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.hani.diktat.database.Word
import me.hani.diktat.repository.DiktatRepository

class WordViewModel(private val repository: DiktatRepository, application: Application) :
    AndroidViewModel(application) {

    fun getCategoryWordsList(cat_id: Int): LiveData<List<Word>> =
        repository.getCategoryWordList(cat_id)

    fun addWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addWord(word)
        }
    }
    fun updateWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateWord(word)
        }
    }
    fun deleteWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWord(word)
        }
    }


}