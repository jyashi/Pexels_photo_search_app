package com.example.pexels_photo_search_app.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexels_photo_search_app.ApiConstants.NO_RESULTS
import com.example.pexels_photo_search_app.ApiConstants.SEARCH_URL
import com.example.pexels_photo_search_app.ApiConstants.UNKNOWN_ERROR
import com.example.pexels_photo_search_app.data.network.ApiClient
import com.example.pexels_photo_search_app.data.network.Photo
import com.example.pexels_photo_search_app.data.network.SearchResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val apiClient: ApiClient) : ViewModel() {

    val searchText = MutableStateFlow("")
    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos
    private var nextPageUrl: String? = null
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    private var currentPage = 1

    fun searchPhotos(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val searchResponse: SearchResponse? = apiClient.searchPhotos(
                    SEARCH_URL,
                    queryParameters = mapOf("query" to query),
                )
                nextPageUrl = searchResponse?.nextPage
                Log.d("MainViewModel", "searchPhotos response: $searchResponse")
                if (searchResponse?.photos?.isNotEmpty() == true) {
                    _photos.value = searchResponse.photos
                } else {
                    _errorMessage.value = NO_RESULTS
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "searchPhotos: ${e.message}", e)
                _errorMessage.value = UNKNOWN_ERROR
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun getPhotoById(photoId: Int): Photo? {
        return _photos.value.find { it.id == photoId }
    }

    fun loadMorePhotos() {
        val url = nextPageUrl
        if (url != null) {
            viewModelScope.launch {
                try {
                    val queryParameters = mapOf(
                        "query" to searchText.value,
                        "page" to currentPage.toString(),
                        "per_page" to "15"
                    )
                    val response = apiClient.searchPhotos(url,queryParameters)
                    nextPageUrl = response?.nextPage
                    if (response != null) {
                        _photos.value = _photos.value + response.photos
                    }
                } catch (e: Exception) {
                    Log.e("MainViewModel", "Error loading more photos: ${e.message}", e)
                }
            }
        }
    }
}
