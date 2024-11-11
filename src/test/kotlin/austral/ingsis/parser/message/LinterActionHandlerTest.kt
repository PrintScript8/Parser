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
import org.springframework.web.client.RestClient.Builder
import kotlin.test.Test

@RestClientTest(LinterActionHandlerTest::class)
class LinterActionHandlerTest {
    private lateinit var actionHandler: ActionHandler

    @Autowired
    private lateinit var server: MockRestServiceServer

    @MockBean
    private lateinit var snippetService: SnippetService

    @Autowired
    private lateinit var clientBuilder: Builder

    @BeforeEach
    fun setUp() {
        actionHandler = ActionHandler(snippetService, clientBuilder)
    }

    @Test
    fun `test handleAnalyze`() {
        // Mocking SnippetService responses
        val snippetId = 1L
        val language = "kotlin"
        val rules = "rules"
        val id = 123L
        val snippet = "fun main() {}"

        `when`(snippetService.getSnippetById(snippetId)).thenReturn(snippet)

        // Mocking RestClient responses
        server.expect(requestTo("http://snippet-service:8080/snippets/status"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess())

        actionHandler.handleAnalyze(listOf(snippetId), language, rules, id)

        // Verify interactions
        verify(snippetService).getSnippetById(snippetId)
        verify(snippetService).analyzeSnippet(snippet, rules, language, snippetId, id)
    }
}
