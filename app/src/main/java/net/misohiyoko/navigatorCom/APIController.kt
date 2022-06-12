package net.misohiyoko.navigatorCom

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
    var GoogleApiKey:String = ""



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
                    id = it.placeId,
                    address = it.formattedAddress
                )
            }


        return locationsNullable ?: listOf<NamedLocation>()

    }

    private suspend fun getPlacesDetailData(placeId:String) : Result<HttpResponse>{
        val urlString = "https://maps.googleapis.com/maps/api/place/details/json"
        val urlBuilder = URLBuilder(urlString)
        urlBuilder.parameters.append("place_id", placeId)
        urlBuilder.parameters.append("key", GoogleApiKey)
        val url = Url(urlBuilder)
        return getJson(url)
    }

    public suspend fun getPlacesDetailResults(placeId: String) : PlacesDetailResponse{
        val placesDetailResponse = getPlacesDetailData(placeId)
        return placesDetailResponse.getOrNull()?.body<PlacesDetailResponse>() ?: PlacesDetailResponse()
    }

}

