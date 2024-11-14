package austral.ingsis.parser.service

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

        // Simulate a valid response for a valid token
        val responseBody: Map<*, *> = mapOf("id" to "user-id")
        val responseEntity: ResponseEntity<Map<*, *>> = ResponseEntity(responseBody, HttpStatus.OK)

        // Mock the restTemplate to return the valid response
        Mockito.`when`(
            restTemplate.exchange(
                "http://authorization-service:8080/authorize/auth0",
                HttpMethod.POST,
                requestEntity,
                Map::class.java,
            ),
        ).thenReturn(responseEntity)

        // Call the method under test
        val result = authService.validateToken(token)

        // Assert the expected result
        assert(result == "user-id")
    }

    @Test
    fun `should return null for unauthorized token`() {
        val token = "unauthorized-token"
        val headers = HttpHeaders()
        headers.set("Authorization", token)
        val requestEntity = HttpEntity<String>(headers)

        // Simulate an unauthorized response
        val responseEntity: ResponseEntity<Map<*, *>> = ResponseEntity(HttpStatus.UNAUTHORIZED)

        // Mock the restTemplate to return the unauthorized response
        Mockito.`when`(
            restTemplate.exchange(
                "http://authorization-service:8080/authorize/auth0",
                HttpMethod.POST,
                requestEntity,
                Map::class.java,
            ),
        ).thenReturn(responseEntity)

        // Call the method under test
        val result = authService.validateToken(token)

        // Assert the expected result (null for unauthorized)
        assertNull(result)
    }

    @Test
    fun `should return null for invalid token response body`() {
        val token = "invalid-token"
        val headers = HttpHeaders()
        headers.set("Authorization", token)
        val requestEntity = HttpEntity<String>(headers)

        // Simulate a valid response but missing the "id" field in the response body
        val responseBody: Map<*, *> = mapOf("otherField" to "someValue")
        val responseEntity: ResponseEntity<Map<*, *>> = ResponseEntity(responseBody, HttpStatus.OK)

        // Mock the restTemplate to return the valid response
        Mockito.`when`(
            restTemplate.exchange(
                "http://authorization-service:8080/authorize/auth0",
                HttpMethod.POST,
                requestEntity,
                Map::class.java,
            ),
        ).thenReturn(responseEntity)

        // Call the method under test
        val result = authService.validateToken(token)

        // Assert the expected result (null because "id" is missing)
        assertNull(result)
    }

    @Test
    fun `should return null for exception during token validation`() {
        val token = "exception-token"
        val headers = HttpHeaders()
        headers.set("Authorization", token)
        val requestEntity = HttpEntity<String>(headers)

        // Simulate an exception being thrown during the exchange call
        Mockito.`when`(
            restTemplate.exchange(
                "http://authorization-service:8080/authorize/auth0",
                HttpMethod.POST,
                requestEntity,
                Map::class.java,
            ),
        ).thenThrow(RuntimeException::class.java)

        // Call the method under test
        val result = authService.validateToken(token)

        // Assert the expected result (null due to exception)
        assertNull(result)
    }
}
