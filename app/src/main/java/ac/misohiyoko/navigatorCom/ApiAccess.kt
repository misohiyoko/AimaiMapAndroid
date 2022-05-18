package ac.misohiyoko.navigatorCom
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.statement.*

val client = HttpClient(CIO){
    engine {
        maxConnectionsCount = 1000
        endpoint{
            maxConnectionsPerRoute = 100
            pipelineMaxSize = 20
            keepAliveTime = 5000
            connectTimeout = 5000
            connectAttempts = 5
        }

    }
    expectSuccess = true;
    install(Logging){
        logger = Logger.ANDROID
        level = LogLevel.HEADERS
    }
}


class ApiAccess {

}