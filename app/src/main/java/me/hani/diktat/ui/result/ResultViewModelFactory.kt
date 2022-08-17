package me.hani.diktat.ui.result

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.hani.diktat.repository.DiktatRepository


class ResultViewModelFactory(val repository: DiktatRepository, val application: Application) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
            return ResultViewModel(repository, application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}