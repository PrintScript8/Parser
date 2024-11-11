package austral.ingsis.parser.service

import austral.ingsis.parser.message.SimpleTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(SnippetService::class)
class SnippetServiceTests {
    @Autowired
    private lateinit var snippetService: SnippetService

    @Autowired
    private lateinit var mockServer: MockRestServiceServer

    @Test
    fun `test validateSnippet`() {
        val snippet = "let a: number = 2;"
        val language = "printscript"

        val result = snippetService.validateSnippet(snippet, language)

        assertNotNull(result)
        assertEquals(true, result)
    }

    @Test
    fun `test formatSnippet`() {
        val snippetId = 1L
        val snippet = "let a: number = 2;"
        val formattedSnippet = "let a:number=2;"
        val language = "printscript"
        val config =
            "{\n" +
                "    \"rules\": {\n" +
                "        \"spaceBeforeColon\": false,\n" +
                "        \"spaceAfterColon\": false,\n" +
                "        \"spaceAroundEquals\": false,\n" +
                "        \"newlineBeforePrintln\": 1\n" +
                "    }\n" +
                "}"
        val token = "testToken"

        mockServer.expect(ExpectedCount.once(), requestTo("http://asset-service:8080/v1/asset/snippet/$snippetId"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess())
        mockServer.expect(ExpectedCount.once(), requestTo("http://snippet-service:8080/snippets/status"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess())

        val result = snippetService.formatSnippet(snippet, language, snippetId, config, token)

        assertEquals(formattedSnippet, result)
        mockServer.verify()
    }

    @Test
    fun `test analyzeSnippet`() {
        val snippetId = 1L
        val snippet = "let a: number = 2;"
        val language = "printscript"
        val config = "{}"
        val token = "testToken"

        mockServer.expect(ExpectedCount.once(), requestTo("http://snippet-service:8080/snippets/status"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess())

        snippetService.analyzeSnippet(snippet, config, language, snippetId, token)

        // Verify the status update
        mockServer.verify()
    }

    @Test
    fun `test getSnippetById`() {
        val snippetId = 1L
        val snippetContent = "let a: number = 2;"

        mockServer.expect(ExpectedCount.once(), requestTo("http://asset-service:8080/v1/asset/snippet/$snippetId"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(snippetContent, MediaType.TEXT_PLAIN))

        val result = snippetService.getSnippetById(snippetId)

        assertNotNull(result)
        assertEquals(snippetContent, result)
    }

    @Test
    fun `test getTests`() {
        val snippetId = 1L
        val expectedResponse = "[{\"name\":\"Test 1\",\"input\":[\"input1\"],\"output\":[\"output1\"]}]"
        val token = "testToken"

        mockServer.expect(ExpectedCount.once(), requestTo("http://snippet-service:8080/test/retrieve/$snippetId"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON))

        val result = snippetService.getTests(snippetId, token)

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(listOf("input1"), result[0].input)
        assertEquals(listOf("output1"), result[0].output)
    }

    @Test
    fun `test executeTest`() {
        val code = "let a: number = 2;"
        val language = "printscript"
        val input = listOf("input1")

        val result = snippetService.executeTest(code, language, input)

        assertNotNull(result)
        // Adjust assertion based on actual behavior
    }

    @Test
    fun `test executeMultipleTests`() {
        val code = "let a: number = 2;"
        val language = "printscript"
        val snippetId = 1L
        val token = "testToken"
        val tests = listOf(SimpleTest(listOf("input1"), listOf("output1")))

        mockServer.expect(ExpectedCount.once(), requestTo("http://snippet-service:8080/snippets/status"))
            .andExpect(method(HttpMethod.PUT))
            .andRespond(withSuccess())

        snippetService.executeMultipleTests(code, language, tests, snippetId, token)
        mockServer.verify()
    }
}
