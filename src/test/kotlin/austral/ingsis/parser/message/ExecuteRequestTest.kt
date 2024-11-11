package austral.ingsis.parser.message

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ExecuteRequestTest {
    @Test
    fun `test ExecuteRequest creation`() {
        val executeRequest =
            ExecuteRequest(
                token = "token",
                language = "Kotlin",
                rules = "rule1",
                action = "execute",
                snippetId = 2L,
            )

        assertEquals("token", executeRequest.token)
        assertEquals("Kotlin", executeRequest.language)
        assertEquals("rule1", executeRequest.rules)
        assertEquals("execute", executeRequest.action)
        assertEquals(2L, executeRequest.snippetId)
    }

    @Test
    fun `test ExecuteRequest creation with null snippetId`() {
        val executeRequest =
            ExecuteRequest(
                token = "token",
                language = "Kotlin",
                rules = "rule1",
                action = "execute",
                snippetId = null,
            )

        assertEquals("token", executeRequest.token)
        assertEquals("Kotlin", executeRequest.language)
        assertEquals("rule1", executeRequest.rules)
        assertEquals("execute", executeRequest.action)
        assertNull(executeRequest.snippetId)
    }
}
