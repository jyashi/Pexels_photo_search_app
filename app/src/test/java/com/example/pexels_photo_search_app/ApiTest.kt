package com.example.pexels_photo_search_app

import com.example.pexels_photo_search_app.ApiConstants.SEARCH_URL
import com.example.pexels_photo_search_app.data.network.ApiClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ApiClientTest {

    private val jsonResponse = """
        {
            "total_results": 1,
            "page": 1,
            "per_page": 15,
            "photos": [
                {
                    "id": 12345,
                    "width": 100,
                    "height": 100,
                    "url": "https://example.com/photo",
                    "photographer": "Navtej Dhot",
                    "photographer_url": "https://example.com/photographer",
                    "photographer_id": 1,
                    "src": {
                        "original": "https://example.com/photo/original",
                        "large2x": "https://example.com/photo/large2x",
                        "large": "https://example.com/photo/large",
                        "medium": "https://example.com/photo/medium",
                        "small": "https://example.com/photo/small",
                        "portrait": "https://example.com/photo/portrait",
                        "landscape": "https://example.com/photo/landscape",
                        "tiny": "https://example.com/photo/tiny"
                    }
                }
            ],
            "next_page": "https://api.pexels.com/v1/search?page=2&per_page=15"
        }
    """.trimIndent()

    /*
        * Tests for : Successful photo response retrieval based on a query
        * Checks for: searchPhotos response to match the expected json response
     */
    @Test
    fun `search photos success`() = runBlocking {
        val mockEngine = MockEngine { _ ->
            respond(
                content = jsonResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(
                    HttpHeaders.ContentType,
                    ContentType.Application.Json.toString()
                )
            )
        }
        val apiClient = ApiClient(mockEngine)

        val response = apiClient.searchPhotos(SEARCH_URL,mapOf("query" to "nature"))

        assertEquals(1, response?.totalResults)
        assertEquals(1, response?.photos?.size)
        assertEquals("Navtej Dhot", response?.photos?.first()?.photographer)
    }

    /*
        * Tests for: Handling failure when a photo search results in a 404 error
        * Checks for: 404 error and returning null
    */
    @Test
    fun `search photos failure returns null on 404 error`() = runBlocking {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "Not Found",
                status = HttpStatusCode.NotFound,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Text.Plain.toString())
            )
        }

        val apiClient = ApiClient(mockEngine)

        val response = apiClient.searchPhotos(SEARCH_URL,mapOf("query" to "0145"))
        assertNull(response)
    }

    /*
        * Tests for: Receiving an empty photo list response from the server
        * Checks for: an empty photos list and return a response with zero total results
     */
    @Test
    fun `search photos with empty response`() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "{\"page\":1,\"per_page\":15,\"photos\":[],\"total_results\":0}",
                status = HttpStatusCode.OK,
                headers = headersOf(
                    HttpHeaders.ContentType,
                    ContentType.Application.Json.toString()
                )
            )
        }
        val apiClient = ApiClient(mockEngine)
        val queryParameters = mapOf("query" to "0145")
        val response = apiClient.searchPhotos(SEARCH_URL,queryParameters)
        assertEquals(0, response?.photos?.size)
        assertEquals(true, response?.photos?.isEmpty())
    }
}


