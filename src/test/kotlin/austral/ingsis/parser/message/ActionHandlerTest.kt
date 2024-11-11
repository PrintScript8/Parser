package austral.ingsis.parser.message

import austral.ingsis.parser.service.SnippetService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import kotlin.test.Test

@RestClientTest(ActionHandler::class)
class ActionHandlerTest {
    private lateinit var actionHandler: ActionHandler

    @Autowired
    private lateinit var server: MockRestServiceServer

    @MockBean
    private lateinit var snippetService: SnippetService

    @Autowired
    private lateinit var clientBuilder: RestClient.Builder

    @BeforeEach
    fun setUp() {
        actionHandler = ActionHandler(snippetService, clientBuilder)
    }

    @Test
    fun `test handleFormat`() {
        // Mocking SnippetService responses
        val snippetId = 1L
        val language = "kotlin"
        val config = "config"
        val token = "testToken"
        val snippet = "fun main() {}"

        `when`(snippetService.getSnippetById(snippetId)).thenReturn(snippet)

        // Mocking RestClient responses
        server.expect(requestTo("http://snippet-service:8080/snippets/status"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess())

        actionHandler.handleFormat(listOf(snippetId), language, config, token)

        // Verify interactions
        verify(snippetService).getSnippetById(snippetId)
        verify(snippetService).formatSnippet(snippet, language, snippetId, config, token)
    }

    @Test
    fun `test handleValidate`() {
        val snippetId = 1L
        val language = "kotlin"
        val snippet = "fun main() {}"

        `when`(snippetService.getSnippetById(snippetId)).thenReturn(snippet)

        actionHandler.handleValidate(listOf(snippetId), language)

        verify(snippetService).getSnippetById(snippetId)
        verify(snippetService).validateSnippet(snippet, language)
    }

    @Test
    fun `test handleExecute`() {
        val snippetId = 1L
        val language = "kotlin"
        val token = "testToken"
        val snippet = "fun main() {}"
        val tests = listOf(SimpleTest(listOf("input"), listOf("output")))

        `when`(snippetService.getSnippetById(snippetId)).thenReturn(snippet)
        `when`(snippetService.getTests(snippetId, token)).thenReturn(tests)

        actionHandler.handleExecute(language, snippetId, token)

        verify(snippetService).getSnippetById(snippetId)
        verify(snippetService).getTests(snippetId, token)
        verify(snippetService).executeMultipleTests(snippet, language, tests, snippetId, token)
    }
}
