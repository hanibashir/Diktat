package me.hani.diktat.ui.category

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.hani.diktat.repository.DiktatRepository


class CatViewModelFactory(
    private val repository: DiktatRepository
) : ViewModelProvider.Factory{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatViewModel::class.java)){
            return CatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}