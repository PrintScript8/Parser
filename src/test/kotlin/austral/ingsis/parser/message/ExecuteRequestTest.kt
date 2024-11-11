package austral.ingsis.parser.message

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ExecuteRequestTest {
    @Test
    fun `test ExecuteRequest creation`() {
        val executeRequest =
            ExecuteRequest(
                ownerId = 1L,
                language = "Kotlin",
                rules = "rule1",
                action = "execute",
                snippetId = 2L,
            )

        assertEquals(1L, executeRequest.ownerId)
        assertEquals("Kotlin", executeRequest.language)
        assertEquals("rule1", executeRequest.rules)
        assertEquals("execute", executeRequest.action)
        assertEquals(2L, executeRequest.snippetId)
    }

    @Test
    fun `test ExecuteRequest creation with null snippetId`() {
        val executeRequest =
            ExecuteRequest(
                ownerId = 1L,
                language = "Kotlin",
                rules = "rule1",
                action = "execute",
                snippetId = null,
            )

        assertEquals(1L, executeRequest.ownerId)
        assertEquals("Kotlin", executeRequest.language)
        assertEquals("rule1", executeRequest.rules)
        assertEquals("execute", executeRequest.action)
        assertNull(executeRequest.snippetId)
    }
}
