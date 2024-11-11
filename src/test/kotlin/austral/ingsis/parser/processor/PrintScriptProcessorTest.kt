package austral.ingsis.parser.processor

import austral.ingsis.parser.exception.ProcessorException
import error.Error
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PrintScriptProcessorTest {
    private val processor = PrintScriptProcessor()

    @Test
    fun `test validate with valid code`() {
        val code = "println(\"Hello, World!\");"
        assertTrue(processor.validate(code))
    }

    @Test
    fun `test validate with invalid code`() {
        val code = "invalid code"
        assertThrows<ProcessorException> {
            processor.validate(code)
        }
    }

    @Test
    fun `test execute with valid code`() {
        val code = "println(\"Hello, World!\");"
        val inputs = listOf("Hello, World!")
        val result = processor.execute(code, inputs)
        assertNotNull(result)
        // Add more assertions based on expected output
    }

    @Test
    fun `test execute with invalid code`() {
        val code = "invalid code"
        val inputs = listOf("input1", "input2")
        assertThrows<ProcessorException> {
            processor.execute(code, inputs)
        }
    }

    @Test
    fun `test format with invalid code`() {
        val code = "invalid code"
        val json = "{}"
        assertThrows<ProcessorException> {
            processor.format(code, json)
        }
    }

    @Test
    fun `test analyze with valid code`() {
        val code = "valid code"
        val config = "{}"
        val result = processor.analyze(code, config)
        assertNotNull(result)
        // Add more assertions based on expected output
    }

    @Test
    fun `test analyze with invalid code`() {
        val code = "invalid code"
        val config = "{}"
        assertEquals(processor.analyze(code, config), emptyList<Error>())
    }

    @Test
    fun `test format with valid code`() {
        val code = "println(\"Hello, World!\");"
        val json =
            "{\n" +
                "    \"rules\": {\n" +
                "        \"spaceBeforeColon\": false,\n" +
                "        \"spaceAfterColon\": false,\n" +
                "        \"spaceAroundEquals\": false,\n" +
                "        \"newlineBeforePrintln\": 1\n" +
                "    }\n" +
                "}"
        val result = processor.format(code, json)
        assertNotNull(result)
    }

    @Test
    fun `test format with invalid json`() {
        val code = "println(\"Hello, World!\");"
        val json = ""
        assertThrows<ProcessorException> {
            processor.format(code, json)
        }
    }

    @Test
    fun `test analyze with invalid config`() {
        val code = "valid code"
        val config = "invalid config"
        assertThrows<ProcessorException> {
            processor.analyze(code, config)
        }
    }
}
