package ac.misohiyoko.navigatorCom

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

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

        val response = client.get(url)
        return response.body()

    }
    public suspend fun getGeocodingData(name:String){
        val urlString = "https://maps.googleapis.com/maps/api/geocode/json?"
        var urlBuilder = URLBuilder(urlString)
        urlBuilder.parameters.append("address", name)
        urlBuilder.parameters.append("key", )
    }

}

