package ac.misohiyoko.navigatorCom

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

object APIController {

    const val GoogleApiKey = "AIzaSyCV_MLSJQRJTR4O8rG2F7bTQL8-vtKCEj4"



    private suspend inline fun getJson(url: Url): Result<HttpResponse> {
        val client = HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json{
                    ignoreUnknownKeys = true
                })
            }
            install(Logging)

        }
        val response: Result<HttpResponse> = kotlin.runCatching {
            withContext(Dispatchers.IO) {
                client.get(url)
            }
        }


        return response

    }

    private suspend fun getGeocodingData(name: String): Result<HttpResponse> {
        val urlString = "https://maps.googleapis.com/maps/api/geocode/json"
        val urlBuilder = URLBuilder(urlString)
        urlBuilder.parameters.append("address", name)
        urlBuilder.parameters.append("key", GoogleApiKey)
        val url = Url(urlBuilder)

        return getJson(url)
    }

    public suspend fun getGeocodingResults(name: String): List<NamedLocation> {

        val geocodingResponse = getGeocodingData(name)


        val locationsNullable =
            geocodingResponse.getOrNull()?.body<GeocodingResponse>()?.results?.map {
                NamedLocation(
                    latitude = it.geometry.location.lat,
                    longitude = it.geometry.location.lng,
                    name = it.placeId
                )
            }


        return locationsNullable ?: listOf<NamedLocation>()

    }

}

