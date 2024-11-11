package austral.ingsis.parser.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ProcessorExceptionTest {
    @Test
    fun `test ProcessorException with message`() {
        val exception = ProcessorException("Test message")
        assertEquals("Test message", exception.message)
        assertNull(exception.cause)
    }

    @Test
    fun `test ProcessorException with message and cause`() {
        val cause = Throwable("Cause message")
        val exception = ProcessorException("Test message", cause)
        assertEquals("Test message", exception.message)
        assertEquals(cause, exception.cause)
    }
}
