package austral.ingsis.parser.processor

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CodeProcessorFactoryTest {
    @Test
    fun `test getProcessor with PrintScript`() {
        val processor = CodeProcessorFactory.getProcessor("printscript")
        assertTrue(processor is PrintScriptProcessor)
    }

    @Test
    fun `test getProcessor with unsupported language`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                CodeProcessorFactory.getProcessor("unsupported")
            }
        assertEquals("Unsupported language: unsupported", exception.message)
    }
}
