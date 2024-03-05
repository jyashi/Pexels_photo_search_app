package com.example.pexels_photo_search_app.data.network

import com.example.pexels_photo_search_app.BuildConfig.PEXELS_API_KEY
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ApiClient(engine: HttpClientEngine) {

    @OptIn(ExperimentalSerializationApi::class)
    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Ignore keys not added to data class
                explicitNulls = false // Workaround for "0" query causing illegal input error
            })
        }
        defaultRequest {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            header("Authorization", PEXELS_API_KEY)
        }
    }

    // Base api function to fetch data from api
    private suspend fun fetchData(
        url: String,
        queryParameters: Map<String, String> = emptyMap(),
        headers: Map<String, String> = emptyMap()
    ): HttpResponse {
        return client.get(url) {
            headers.forEach { (key, value) ->
                header(key, value)
            }
            queryParameters.forEach { (key, value) ->
                parameter(key, value)
            }
        }
    }

    // Returns the response body if response code is successful
    suspend fun searchPhotos(
        url: String,
        queryParameters: Map<String, String>,
        page: Int = 1,
        perPage: Int = 15
    ): SearchResponse? {
        val httpResponse = fetchData(
            url = url,
            queryParameters = queryParameters + mapOf(
                "page" to page.toString(),
                "per_page" to perPage.toString()
            ),
            headers = mapOf("Authorization" to PEXELS_API_KEY)
        )
        return if (httpResponse.status == HttpStatusCode.OK) {
            httpResponse.body()
        } else {
            null
        }
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
    val src: PhotoSrc,
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
