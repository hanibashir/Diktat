package me.hani.diktat.ui.result

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.hani.diktat.repository.DiktatRepository

class ResultViewModel(private val repository: DiktatRepository, application: Application) :
    AndroidViewModel(application) {

    fun updateCorrectWrong(newValue: Int, word_id: Int) {
        viewModelScope.launch {
            repository.updateCorrectWrong(newValue, word_id)
        }
    }
}