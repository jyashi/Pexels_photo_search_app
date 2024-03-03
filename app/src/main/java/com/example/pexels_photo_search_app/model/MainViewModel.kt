package com.example.pexels_photo_search_app.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexels_photo_search_app.data.network.ApiClient
import com.example.pexels_photo_search_app.data.network.Photo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val apiClient: ApiClient) : ViewModel() {

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos // TODO use in lazy column

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading // TODO add loading indicator

    fun searchPhotos(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = apiClient.searchPhotos(query)
                _photos.value = response.photos
            } catch (e: Exception) {
                Log.e("MainViewModel", "searchPhotos: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
