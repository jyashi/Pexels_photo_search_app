package com.example.pexels_photo_search_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pexels_photo_search_app.data.network.ApiClient
import com.example.pexels_photo_search_app.model.MainViewModel
import com.example.pexels_photo_search_app.ui.screen.HomeScreen
import com.example.pexels_photo_search_app.ui.theme.Pexels_photo_search_appTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiClient = ApiClient()
        val viewModel = MainViewModel(apiClient)
        setContent {
            Pexels_photo_search_appTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // TODO add nav graph for navigation
                    HomeScreen(viewModel)
                }
            }
        }
    }
}