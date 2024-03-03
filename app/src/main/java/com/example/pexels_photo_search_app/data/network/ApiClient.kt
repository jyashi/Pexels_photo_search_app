package com.example.pexels_photo_search_app.data.network

import com.example.pexels_photo_search_app.BuildConfig.PEXELS_API_KEY
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ApiClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        defaultRequest {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            header("Authorization", PEXELS_API_KEY)
        }
    }

    suspend fun searchPhotos(query: String, page: Int = 1, perPage: Int = 15): SearchResponse {
        val httpResponse: HttpResponse = client.get("https://api.pexels.com/v1/search") {
            parameter("query", query)
            parameter("page", page)
            parameter("per_page", perPage)
        }
        return httpResponse.body<SearchResponse>()
    }
}
@Serializable
data class SearchResponse(
    @SerialName("total_results") val totalResults: Int,
    val page: Int,
    @SerialName("per_page") val perPage: Int,
    val photos: List<Photo>,
    @SerialName("next_page") val nextPage: String?
)

@Serializable
data class Photo(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    @SerialName("photographer_url") val photographerUrl: String,
    @SerialName("photographer_id") val photographerId: Int,
    @SerialName("avg_color") val avgColor: String,
    val src: PhotoSrc,
    val liked: Boolean,
    val alt: String
)

@Serializable
data class PhotoSrc(
    val original: String,
    @SerialName("large2x") val large2X: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String
)
