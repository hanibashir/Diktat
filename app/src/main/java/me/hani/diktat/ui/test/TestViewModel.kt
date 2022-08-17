package me.hani.diktat.ui.test

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.hani.diktat.database.Word
import me.hani.diktat.repository.DiktatRepository

class TestViewModel(private val repository: DiktatRepository, application: Application) :
    AndroidViewModel(application) {

    // get category
    fun getCategoryWordsList(cat_id: Int): LiveData<List<Word>> =
        repository.getCategoryWordList(cat_id)
}