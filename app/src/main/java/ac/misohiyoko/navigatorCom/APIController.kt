package ac.misohiyoko.navigatorCom

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.json.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

class APIController {
    companion object{


    }
    private fun getJson(url: Url){
        val client = HttpClient(CIO){
            expectSuccess = true
            install(ContentNegotiation){
                json()
            }
            install(Logging)

        }



    }
}

