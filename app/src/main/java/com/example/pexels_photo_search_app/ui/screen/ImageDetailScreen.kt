package com.example.pexels_photo_search_app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.pexels_photo_search_app.model.MainViewModel

@Composable
fun ImageDetailScreen(photoId: Int?, viewModel: MainViewModel) {
    val photo = viewModel.getPhotoById(photoId ?: -1)
    Box(contentAlignment = Alignment.BottomStart) {
        photo?.src?.let { src ->
            val imagePainter = rememberAsyncImagePainter(src.original)
            Image(
                painter = imagePainter,
                contentDescription = "Photo by ${photo.photographer}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        photo?.let {
            Text(
                text = "Photo by ${it.photographer}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White
            )
        }
    }
}




