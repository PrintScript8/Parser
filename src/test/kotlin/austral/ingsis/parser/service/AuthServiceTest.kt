package austral.ingsis.parser.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.RestTemplate

class AuthServiceTest {
    private lateinit var authService: AuthService
    private lateinit var restTemplate: RestTemplate
    private lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        restTemplate = Mockito.spy(RestTemplate()) // We spy on RestTemplate to track its interactions
        authService = AuthService()

        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    @Test
    fun `test validateToken with UnknownHostException`() {
        // Arrange
        val token = "Bearer invalidToken"
        mockServer.expect(MockRestRequestMatchers.requestTo("http://authorization:8087/authorize/auth0"))
            .andRespond(MockRestResponseCreators.withStatus(HttpStatus.INTERNAL_SERVER_ERROR)) // Simulate server error

        // Act
        val response = authService.validateToken(token)

        // Assert
        assert(response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun `test validateToken with ResourceAccessException`() {
        // Arrange
        val token = "Bearer invalidToken"
        mockServer.expect(MockRestRequestMatchers.requestTo("http://authorization:8087/authorize/auth0"))
            .andRespond(
                MockRestResponseCreators.withStatus(HttpStatus.INTERNAL_SERVER_ERROR),
            )

        // Act
        val response = authService.validateToken(token)

        // Assert
        assert(response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
