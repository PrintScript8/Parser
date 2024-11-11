package austral.ingsis.parser.service

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate

class AuthServiceTest {
    private lateinit var authService: AuthService
    private lateinit var restTemplate: RestTemplate

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        restTemplate = Mockito.mock(RestTemplate::class.java)
        authService = AuthService()
        authService.restTemplate = restTemplate
    }

    @Test
    fun `should return user id for valid token`() {
        val token = "valid-token"
        val headers = HttpHeaders()
        headers.set("Authorization", token)
        val requestEntity = HttpEntity<String>(headers)
        // val responseBody = mapOf("id" to "user-id")
        // val responseEntity = ResponseEntity(responseBody, HttpStatus.OK)

        Mockito.`when`(
            restTemplate.exchange(
                "http://authorization-service:8080/authorize/auth0",
                HttpMethod.POST,
                requestEntity,
                Map::class.java,
            ),
        )

        val result = authService.validateToken(token)
    }

    @Test
    fun `should return null for unauthorized token`() {
        val token = "unauthorized-token"
        val headers = HttpHeaders()
        headers.set("Authorization", token)
        val requestEntity = HttpEntity<String>(headers)
        // val responseEntity = ResponseEntity<Map<String, Any>>(HttpStatus.UNAUTHORIZED)

        Mockito.`when`(
            restTemplate.exchange(
                "http://authorization-service:8080/authorize/auth0",
                HttpMethod.POST,
                requestEntity,
                Map::class.java,
            ),
        )

        val result = authService.validateToken(token)
        assertNull(result)
    }

    @Test
    fun `should return null for exception`() {
        val token = "exception-token"
        val headers = HttpHeaders()
        headers.set("Authorization", token)
        val requestEntity = HttpEntity<String>(headers)

        Mockito.`when`(
            restTemplate.exchange(
                "http://authorization-service:808/authorize/auth0",
                HttpMethod.POST,
                requestEntity,
                Map::class.java,
            ),
        ).thenThrow(RuntimeException::class.java)

        val result = authService.validateToken(token)
        assertNull(result)
    }
}
