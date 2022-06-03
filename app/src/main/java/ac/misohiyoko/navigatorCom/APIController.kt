package ac.misohiyoko.navigatorCom

import android.location.Location
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

class APIController {
    companion object{
        const val GoogleApiKey = "AIzaSyCV_MLSJQRJTR4O8rG2F7bTQL8-vtKCEj4"

    }

    private suspend inline fun <reified T : ISerializableDummy> getJson(url: Url): T {
        val client = HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                json()
            }
            install(Logging)

        }
        val response:HttpResponse;
        withContext(Dispatchers.IO) {
            response = client.get(url)
        }
        return response.body()

    }
    private suspend fun getGeocodingData(name: String): GeocodingResponse {
        val urlString = "https://maps.googleapis.com/maps/api/geocode/json"
        val urlBuilder = URLBuilder(urlString)
        urlBuilder.parameters.append("address", name)
        urlBuilder.parameters.append("key", GoogleApiKey)
        val url = Url(urlBuilder)
        return getJson(url)
    }

    public suspend fun getGeocodingResults(name: String):List<NamedLocation>{
        val geocodingResponse = getGeocodingData(name)
        val locations = geocodingResponse.results.map {
            NamedLocation(latitude = it.geometry.location.lat, longitude = it.geometry.location.lng, name = it.placeId)
        }
        return locations

    }

}

