package austral.ingsis.parser.controller

import austral.ingsis.parser.service.SnippetService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient

@RestController
@RequestMapping("/parser")
class ParserController(
    @Autowired private val clientBuilder: RestClient.Builder,
) {
    private val snippetService: SnippetService = SnippetService(clientBuilder)

    @PutMapping("/validate")
    fun validateSnippet(
        @RequestBody snippet: ExecuteSnippet,
    ): ResponseEntity<String> {
        val isValid = snippetService.validateSnippet(snippet.code, snippet.language)
        return if (isValid) {
            ResponseEntity.ok("Snippet is valid")
        } else {
            ResponseEntity.badRequest().body("Snippet is not valid")
        }
    }

    @PutMapping("/format")
    fun formatSnippet(
        @RequestBody formatRequest: FormatRequest,
        @RequestHeader("Authorization") token: String,
    ): ResponseEntity<String> {
        val formattedSnippet =
            snippetService.formatSnippet(
                formatRequest.code,
                formatRequest.language,
                formatRequest.id,
                formatRequest.config,
                token,
            )
        return ResponseEntity.ok(formattedSnippet)
    }

    @PutMapping("/test/execute")
    fun executeTest(
        @RequestBody test: TestRequest,
    ): ResponseEntity<List<String>> {
        val response = snippetService.executeTest(test.code, test.language, test.input)
        return ResponseEntity.ok(response)
    }
}

data class ExecuteSnippet(val code: String, val language: String)

data class TestRequest(val code: String, val language: String, val input: List<String>)

data class FormatRequest(val code: String, val language: String, val id: Long, val config: String)
