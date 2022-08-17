package me.hani.diktat.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.hani.diktat.database.Category
import me.hani.diktat.database.CategoryWithWords
import me.hani.diktat.database.Word
import me.hani.diktat.repository.DiktatRepository

class CatViewModel(private val repository: DiktatRepository) :
    ViewModel() {


    val getCatsListWithWordsList: LiveData<List<CategoryWithWords>> =
        repository.getCatsListWithWordsList

    fun getWordsListWithIsCorrect(isCorrect: Int) =
        repository.getWordsListWithIsCorrect(isCorrect)

    //add category
    fun addCat(cat: Category) {
        viewModelScope.launch(Dispatchers.IO) {

            repository.addCat(cat)
        }
    }


    fun deleteCat(cat: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCat(cat)
        }

    }


    fun updateCat(cat: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCat(cat)
        }

    }

    fun deleteWordWithCat(cat_id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWordWithCat(cat_id)
        }
    }


}