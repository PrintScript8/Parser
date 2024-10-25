package austral.ingsis.parser.message

import austral.ingsis.parser.processor.CodeProcessor
import austral.ingsis.parser.model.Snippet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.web.client.RestClient

class ActionHandler(
    private val processor: CodeProcessor,
    private val record: ObjectRecord<String, ExecuteRequest>,
    @Autowired private val restClientBuilder: RestClient.Builder,
) {

    private val bucketClient = restClientBuilder.baseUrl("http://asset-service:8080").build()
    private val snippetClient = restClientBuilder.baseUrl("http://snippet-service:8082").build()

    fun handleValidate() {
        val snippet: Snippet? = bucketClient.get()
            .uri("/v1/asset/{container}/{key}", "snippet", record.value.codeId)
            .retrieve()
            .body(Snippet::class.java)
        if (snippet != null) {
            val isValid = processor.validate(snippet.code)
        }
    }

    fun handleFormat() {
        val snippet: Snippet? = bucketClient.get()
            .uri("/v1/asset/{container}/{key}", "snippet", record.value.codeId)
            .retrieve()
            .body(Snippet::class.java)
        if (snippet != null) {
            val formattedCode: String = processor.format(snippet.code)
            snippet.code = formattedCode
            snippetClient.put()
                .uri("/v1/asset/{container}/{key}", "snippet", snippet.id)
                .body(snippet)
                .retrieve()
                .body(Snippet::class.java)
        }
    }

    fun handleExecute() {}

    fun handleAnalyze() {}
}