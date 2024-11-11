package austral.ingsis.parser.service

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestTemplate
import java.net.UnknownHostException

@Service
class AuthService {
    private val restTemplate = RestTemplate()

    fun validateToken(token: String): ResponseEntity<String> {
        return try {
            // Prepare headers with the Authorization token
            val headers = HttpHeaders()
            headers.set("Authorization", token)
            val requestEntity = HttpEntity<String>(headers)

            // Send request to authorization service
            val response =
                restTemplate.exchange(
                    "http://authorization:8087/authorize/auth0",
                    HttpMethod.POST,
                    requestEntity,
                    Map::class.java,
                )

            // Extract 'id' from the response body
            val responseBody = response.body
            val id = responseBody?.get("id") as? String ?: "No ID returned"

            ResponseEntity.ok("Received ID: $id")
        } catch (e: UnknownHostException) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown host: ${e.message}")
        } catch (e: ResourceAccessException) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Resource access error: ${e.message}")
        }
    }
}
