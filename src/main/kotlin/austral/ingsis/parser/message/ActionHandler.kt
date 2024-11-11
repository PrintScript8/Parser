package austral.ingsis.parser.message

import austral.ingsis.parser.service.SnippetService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestClient

class ActionHandler(
    @Autowired private val snippetService: SnippetService,
    @Autowired private val restClientBuilder: RestClient.Builder,
) {
    private val snippetClient: RestClient = restClientBuilder.baseUrl("http://snippet-service:8080").build()

    fun handleValidate(
        snippets: List<Long>,
        language: String,
    ) {
        for (snippetId in snippets) {
            val snippet: String = snippetService.getSnippetById(snippetId)
            snippetService.validateSnippet(snippet, language)
        }
    }

    fun handleFormat(
        snippets: List<Long>,
        language: String,
        config: String,
        token: String,
    ) {
        for (snippetId in snippets) {
            snippetClient.put()
                .uri("/snippets/status")
                .headers { headers -> headers.set("Authorization", token) }
                .body(SetStatus(snippetId, "pending"))
                .retrieve()
                .toEntity(String::class.java)
        }
        for (snippetId in snippets) {
            val snippet: String = snippetService.getSnippetById(snippetId)
            snippetService.formatSnippet(snippet, language, snippetId, config, token)
        }
    }

    fun handleExecute(
        language: String,
        snippetId: Long?,
        token: String,
    ) {
        if (snippetId == null) {
            throw NoSuchElementException("Snippet id is null")
        }
        val snippet: String = snippetService.getSnippetById(snippetId)
        val tests: List<SimpleTest> = snippetService.getTests(snippetId, token)
        snippetService.executeMultipleTests(snippet, language, tests, snippetId, token)
    }

    fun handleAnalyze(
        snippets: List<Long>,
        language: String,
        rules: String,
        token: String,
    ) {
        for (snippetId in snippets) {
            snippetClient.put()
                .uri("/snippets/status")
                .headers { headers -> headers.set("Authorization", token) }
                .body(SetStatus(snippetId, "pending"))
                .retrieve()
                .toEntity(String::class.java)
        }
        for (snippetId in snippets) {
            val snippet: String = snippetService.getSnippetById(snippetId)
            snippetService.analyzeSnippet(snippet, rules, language, snippetId, token)
        }
    }
}

data class SetStatus(val id: Long, val status: String)

data class SimpleTest(val input: List<String>, val output: List<String>)
