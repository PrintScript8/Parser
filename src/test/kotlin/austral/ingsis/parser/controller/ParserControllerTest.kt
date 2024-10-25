package austral.ingsis.parser.controller

import austral.ingsis.parser.processor.CodeProcessor
import austral.ingsis.parser.processor.CodeProcessorFactory
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ParserControllerTest {
    private val codeProcessorFactory = mockk<CodeProcessorFactory>()
    private val codeProcessor = mockk<CodeProcessor>()
    private val parserController = ParserController()

    private val code = "println( 2 );"
    private val language = "printscript"
    private val config = "{ \"identifier_format\": \"camel case\"}"

    @Test
    fun `validateSnippet should return OK if snippet is valid`() {
        val request = SnippetRequest(code, language, config)
        every { codeProcessorFactory.getProcessor(request.language) } returns codeProcessor
        every { codeProcessor.validate(request.code) } returns true

        val response = parserController.validateSnippet(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Valid Snippet", response.body)
    }

    @Test
    fun `validateSnippet should return BadRequest if snippet is invalid`() {
        val wrongCode = "println(2)"
        val request = SnippetRequest(wrongCode, language, config)
        every { codeProcessorFactory.getProcessor(request.language) } returns codeProcessor
        every { codeProcessor.validate(request.code) } returns false

        val response = parserController.validateSnippet(request)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(
            "Invalid Snippet: Missing ';' at end of statement: row=1, startColumn=10, endColumn=10",
            response.body,
        )
    }

    @Test
    fun `formatSnippet should return formatted code`() {
        val request = SnippetRequest(code, language, config)
        every { codeProcessorFactory.getProcessor(request.language) } returns codeProcessor
        every { codeProcessor.format(request.code) } returns "formattedCode"

        val response = parserController.formatSnippet(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("println(2);", response.body)
    }

    @Test
    fun `executeSnippet should return execution output`() {
        val request = SnippetRequest(code, language, config)
        every { codeProcessorFactory.getProcessor(request.language) } returns codeProcessor
        every { codeProcessor.execute(request.code) } returns listOf("output")

        val response = parserController.executeSnippet(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(listOf("2"), response.body)
    }

    @Test
    fun `analyzeSnippet should return analysis errors`() {
        val request = SnippetRequest(code, language, config)
        val errorType = mockk<error.Type>() // Mock or create a valid Type instance
        every { codeProcessorFactory.getProcessor(request.language) } returns codeProcessor
        every { codeProcessor.analyze(request.code, request.config) } returns listOf(error.Error(errorType, "error"))

        val response = parserController.analyzeSnippet(request)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(emptyList<Error>(), response.body)
    }
}
