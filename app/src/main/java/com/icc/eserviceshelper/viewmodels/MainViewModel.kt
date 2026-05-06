package com.icc.eserviceshelper.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.icc.eserviceshelper.models.Category
import com.icc.eserviceshelper.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class MainViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    
    private val _searchQuery = MutableStateFlow("")
    
    // Original data from Firebase
    private val _allCategories = repository.getCategories()
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.success(emptyList()))

    // Filtered data computed on a background thread
    val filteredCategories = combine(_allCategories, _searchQuery) { result, query ->
        result.map { categories ->
            if (query.isBlank()) {
                categories
            } else {
                categories.filter { category ->
                    category.title.contains(query, ignoreCase = true) ||
                            category.items?.values?.any { item ->
                                item.title.contains(query, ignoreCase = true) ||
                                        item.keywords?.any { it.contains(query, ignoreCase = true) } == true
                            } == true
                }
            }
        }
    }.flowOn(Dispatchers.Default) // Perform filtering on Default dispatcher
     .asLiveData()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
