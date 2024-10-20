package austral.ingsis.parser.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ParserController::class)
class Parser {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun invalidCode() {
        mockMvc.perform(
            post("/parser/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\": \"print(1);\", \"language\": \"print script\"}"),
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Snippet is invalid"))
    }

    @Test
    fun validCode() {
        mockMvc.perform(
            post("/parser/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\": \"println(1);\", \"language\": \"print script\"}"),
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Snippet is valid"))
    }
}
