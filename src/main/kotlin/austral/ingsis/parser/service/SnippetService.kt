package austral.ingsis.parser.service

import austral.ingsis.parser.message.SetStatus
import austral.ingsis.parser.message.SimpleTest
import austral.ingsis.parser.processor.CodeProcessorFactory
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class SnippetService(
    @Autowired final val restClientBuilder: RestClient.Builder,
) {
    var bucketClient: RestClient = restClientBuilder.baseUrl("http://asset-service:8080").build()
    var snippetClient: RestClient = restClientBuilder.baseUrl("http://snippet-service:8080").build()
    val logger: Logger = LogManager.getLogger(SnippetService::class.java)

    fun validateSnippet(
        snippet: String,
        language: String,
    ): Boolean {
        return CodeProcessorFactory.getProcessor(language).validate(snippet)
    }

    fun formatSnippet(
        snippet: String,
        language: String,
        snippetId: Long,
        config: String,
        id: Long,
    ): String {
        val formattedSnippet = CodeProcessorFactory.getProcessor(language).format(snippet, config)
        bucketClient.put()
            .uri("/v1/asset/{container}/{key}", "snippet", snippetId)
            .body(formattedSnippet)
            .retrieve()

        snippetClient.put()
            .uri("/snippets/status")
            .headers { headers -> headers.set("id", id.toString()) }
            .body(SetStatus(snippetId, "compliant"))
            .retrieve()
        return formattedSnippet
    }

    fun analyzeSnippet(
        snippet: String,
        config: String,
        language: String,
        snippetId: Long,
        id: Long,
    ) {
        val result = CodeProcessorFactory.getProcessor(language).analyze(snippet, config)
        if (result.isNotEmpty()) {
            snippetClient.put()
                .uri("/snippets/status")
                .headers { headers -> headers.set("id", id.toString()) }
                .body(SetStatus(snippetId, "non-compliant"))
                .retrieve()
        } else {
            snippetClient.put()
                .uri("/snippets/status")
                .headers { headers -> headers.set("id", id.toString()) }
                .body(SetStatus(snippetId, "compliant"))
                .retrieve()
        }
    }

    fun getSnippetById(id: Long): String {
        val snippet: String? =
            bucketClient.get()
                .uri("/v1/asset/{container}/{key}", "snippet", id)
                .retrieve()
                .body(String::class.java)
        return snippet ?: throw NotFoundException()
    }

    fun getTests(snippetId: Long): List<SimpleTest> {
        val response =
            snippetClient.get()
                .uri("/test/retrieve/{id}", snippetId)
                .retrieve()
                .body(object : ParameterizedTypeReference<List<SimpleTest>>() {})
        return response ?: throw NotFoundException()
    }

    fun executeTest(
        code: String,
        language: String,
        input: List<String>,
    ): List<String> {
        return CodeProcessorFactory.getProcessor(language).execute(code, input)
    }

    @Suppress("MagicNumber")
    fun executeMultipleTests(
        code: String,
        language: String,
        tests: List<SimpleTest>,
        snippetId: Long,
        id: Long,
    ) {
        for (test in tests) {
            val response = CodeProcessorFactory.getProcessor(language).execute(code, test.input)
            logger.info("Response: $response - Expected: ${test.output}")
            if (response != test.output) {
                snippetClient.put()
                    .uri("/snippets/status")
                    .headers { headers -> headers.set("id", id.toString()) }
                    .body(SetStatus(snippetId, "failed"))
                    .retrieve()
                Thread.sleep(1000)
                return
            }
        }
        snippetClient.put()
            .uri("/snippets/status")
            .headers { headers -> headers.set("id", id.toString()) }
            .body(SetStatus(snippetId, "compliant"))
            .retrieve()
    }
}
