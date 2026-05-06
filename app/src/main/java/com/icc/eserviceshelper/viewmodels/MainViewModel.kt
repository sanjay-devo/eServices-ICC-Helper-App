package com.icc.eserviceshelper.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.icc.eserviceshelper.models.Category
import com.icc.eserviceshelper.repository.FirebaseRepository
import com.icc.eserviceshelper.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class MainViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    
    private val _searchQuery = MutableStateFlow("")
    
    // Original data from Firebase converted to UiState
    private val _allCategories = repository.getCategories()
        .map { result ->
            result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Unknown error") }
            )
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    // Filtered data computed on a background thread, maintaining UiState
    val filteredCategories = combine(_allCategories, _searchQuery) { state, query ->
        when (state) {
            is UiState.Success -> {
                val categories = state.data
                val filtered = if (query.isBlank()) {
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
                UiState.Success(filtered)
            }
            is UiState.Loading -> UiState.Loading
            is UiState.Error -> state
        }
    }.flowOn(Dispatchers.Default)
     .asLiveData()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
