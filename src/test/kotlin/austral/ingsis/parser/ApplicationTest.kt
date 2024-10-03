package austral.ingsis.parser

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import kotlin.test.assertNotNull

@SpringBootTest
class ApplicationTest {
    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    fun contextLoads() {
        assertNotNull(context, "The application context should have loaded.")
    }

    @Test
    fun testMain() {
        // Call the main method with empty arguments
        main(arrayOf())
    }
}
