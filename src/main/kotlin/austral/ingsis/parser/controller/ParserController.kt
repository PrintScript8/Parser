package austral.ingsis.parser.controller

import austral.ingsis.parser.processor.CodeProcessorFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class SnippetRequest(
    val code: String,
    val language: String,
    val config: String,
)

@RestController
@RequestMapping("/parser")
class ParserController {

    @PostMapping("/validate")
    fun validateSnippet(
        @RequestBody request: SnippetRequest,
    ): ResponseEntity<String> {
        return try {
            val processor = CodeProcessorFactory.getProcessor(request.language)
            if (processor.validate(request.code)) {
                ResponseEntity.ok("Snippet is valid")
            } else {
                ResponseEntity.badRequest().body("Snippet is invalid")
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body("Unsupported language")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during validation")
        }
    }

    @PostMapping("/format")
    fun formatSnippet(
        @RequestBody request: SnippetRequest,
    ): ResponseEntity<String> {
        val processor = CodeProcessorFactory.getProcessor(request.language)
        return try {
            val formattedCode = processor.format(request.code)
            ResponseEntity.ok(formattedCode)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body("Error during formatting")
        }
    }

    @PostMapping("/execute")
    fun executeSnippet(
        @RequestBody request: SnippetRequest,
    ): ResponseEntity<List<String>> {
        val processor = CodeProcessorFactory.getProcessor(request.language)
        return try {
            val output = processor.execute(request.code)
            ResponseEntity.ok(output)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(null)
        }
    }

    @PostMapping("/analyze")
    fun analyzeSnippet(
        @RequestBody request: SnippetRequest,
    ): ResponseEntity<List<error.Error>> {
        val processor = CodeProcessorFactory.getProcessor(request.language)

        return try {
            val errors = processor.analyze(request.code, request.config)
            ResponseEntity.ok(errors)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(null)
        }
    }
}
