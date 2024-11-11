package austral.ingsis.parser.controller

import austral.ingsis.parser.service.AuthService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(CommunicationController::class)
class CommunicationControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var authService: AuthService

    @Test
    fun `test respondMessage endpoint`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/testRespondMessage"))
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("Greetings from Parser!"))
    }

    @Test
    fun `test validateToken endpoint with valid token`() {
        val token = "valid-token"
        Mockito.`when`(authService.validateToken(token)).thenReturn(ResponseEntity("Valid token", HttpStatus.OK))

        mockMvc.perform(
            MockMvcRequestBuilders.get("/testValidation")
                .header(HttpHeaders.AUTHORIZATION, token),
        )
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("Valid token"))
    }

    @Test
    fun `test validateToken endpoint with invalid token`() {
        val token = "invalid-token"
        Mockito.`when`(authService.validateToken(token))
            .thenReturn(ResponseEntity("Invalid token", HttpStatus.UNAUTHORIZED))

        mockMvc.perform(
            MockMvcRequestBuilders.get("/testValidation")
                .header(HttpHeaders.AUTHORIZATION, token),
        )
            .andExpect(status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.content().string("Invalid token"))
    }
}
