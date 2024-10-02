package austral.ingsis.parser.controller

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Communication {
    @Test
    fun test() {
        val communicationController = CommunicationController()
        assertEquals(communicationController.respondMessage().body, "Greetings from Parser!")
    }
}
