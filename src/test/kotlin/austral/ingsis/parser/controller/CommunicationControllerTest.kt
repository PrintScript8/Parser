package austral.ingsis.parser.controller

import austral.ingsis.parser.service.AuthService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
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
        Mockito.`when`(authService.validateToken(token)).thenReturn("Valid token")

        mockMvc.perform(
            MockMvcRequestBuilders.get("/testValidation")
                .header(HttpHeaders.AUTHORIZATION, token),
        )
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("Token is valid"))
    }

    @Test
    fun `test validateToken endpoint with invalid token`() {
        val token = "invalid-token"
        `when`(authService.validateToken(token))
            .thenReturn(null)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/testValidation")
                .header(HttpHeaders.AUTHORIZATION, token),
        )
            .andExpect(status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string("Token is invalid"))
    }
}
