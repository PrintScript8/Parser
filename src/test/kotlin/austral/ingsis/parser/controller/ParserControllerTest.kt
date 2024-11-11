package austral.ingsis.parser.controller

import austral.ingsis.parser.service.SnippetService
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClient
import java.lang.reflect.Field

@ExtendWith(MockitoExtension::class)
class ParserControllerTest {
    @Mock
    private lateinit var clientBuilder: RestClient.Builder

    @Mock
    private lateinit var restClient: RestClient

    @Mock
    private lateinit var snippetService: SnippetService

    @Mock
    private lateinit var httpServletRequest: HttpServletRequest

    private lateinit var parserController: ParserController

    @BeforeEach
    fun setup() {
        // Mock the builder chain
        `when`(clientBuilder.baseUrl(any())).thenReturn(clientBuilder)
        `when`(clientBuilder.build()).thenReturn(restClient)

        // Create the controller
        parserController = ParserController(clientBuilder)

        // Use reflection to inject our mocked snippetService
        val snippetServiceField: Field = ParserController::class.java.getDeclaredField("snippetService")
        snippetServiceField.isAccessible = true
        snippetServiceField.set(parserController, snippetService)
    }

    @Test
    fun `validateSnippet returns OK when snippet is valid`() {
        // Arrange
        val snippet = ExecuteSnippet("code", "kotlin")
        `when`(snippetService.validateSnippet(snippet.code, snippet.language)).thenReturn(true)

        // Act
        val response = parserController.validateSnippet(snippet)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Snippet is valid", response.body)
        verify(snippetService).validateSnippet(snippet.code, snippet.language)
    }

    @Test
    fun `validateSnippet returns BAD_REQUEST when snippet is invalid`() {
        // Arrange
        val snippet = ExecuteSnippet("invalid code", "kotlin")
        `when`(snippetService.validateSnippet(snippet.code, snippet.language)).thenReturn(false)

        // Act
        val response = parserController.validateSnippet(snippet)

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Snippet is not valid", response.body)
        verify(snippetService).validateSnippet(snippet.code, snippet.language)
    }

    @Test
    fun `formatSnippet returns formatted code`() {
        // Arrange
        val formatRequest = FormatRequest("code", "kotlin", 1L, "config")
        val userId = 123L
        val expectedResponse = "formatted code"

        `when`(httpServletRequest.getHeader("id")).thenReturn(userId.toString())
        `when`(
            snippetService.formatSnippet(
                formatRequest.code,
                formatRequest.language,
                formatRequest.id,
                formatRequest.config,
                userId,
            ),
        ).thenReturn(expectedResponse)

        // Act
        val response = parserController.formatSnippet(formatRequest, httpServletRequest)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedResponse, response.body)
        verify(snippetService).formatSnippet(
            formatRequest.code,
            formatRequest.language,
            formatRequest.id,
            formatRequest.config,
            userId,
        )
    }

    @Test
    fun `executeTest returns test results`() {
        // Arrange
        val testRequest = TestRequest("test code", "kotlin", listOf("input1", "input2"))
        val expectedResponse = listOf("result1", "result2")

        `when`(
            snippetService.executeTest(
                testRequest.code,
                testRequest.language,
                testRequest.input,
            ),
        ).thenReturn(expectedResponse)

        // Act
        val response = parserController.executeTest(testRequest)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expectedResponse, response.body)
        verify(snippetService).executeTest(
            testRequest.code,
            testRequest.language,
            testRequest.input,
        )
    }
}
