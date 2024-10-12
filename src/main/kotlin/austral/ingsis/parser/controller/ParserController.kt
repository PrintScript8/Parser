package austral.ingsis.parser.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import runner.Operations
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

data class SnippetRequest(
    val code: String,
    val language: String
)

@RestController("/parser")
class ParserController {

    @PostMapping("/validate")
    fun validateSnippet(@RequestBody request: SnippetRequest): ResponseEntity<String> {
        val codeInputStream = ByteArrayInputStream(request.code.toByteArray(StandardCharsets.UTF_8))
        val runner = Operations(codeInputStream, "1.1.0", null)

        val isValid = runner.validate()

        return if (isValid.isEmpty()) {
            ResponseEntity.ok("Snippet is valid")
        } else {
            ResponseEntity.badRequest().body("Snippet is invalid")
        }
    }
}
