package ac.misohiyoko.navigatorCom

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.json.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

class APIController {
    companion object{


    }
    private suspend fun getJsonGeocoding(url: Url){
        val client = HttpClient(CIO){
            expectSuccess = true
            install(ContentNegotiation){
                json()
            }
            install(Logging)

        }

        val response = client.get(url)
        val responseAsJson = response.body<GeocodingResponse>()


    }
}

