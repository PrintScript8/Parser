package austral.ingsis.parser.message

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExecuteRequestTest {
    @Test
    fun `should create ExecuteRequest object with correct properties`() {
        // Arrange
        val language = "Kotlin"
        val code = "println('Hello World')"
        val action = "execute"
        val inputs = "[]"

        // Act
        val request = ExecuteRequest(language, code, action, inputs)

        // Assert
        assertEquals(language, request.language)
        assertEquals(code, request.code)
        assertEquals(action, request.action)
        assertEquals(inputs, request.inputs)
    }
}
