package austral.ingsis.parser.message

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig
class ParserRequestHandlerTest {
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var logger: Logger
    private lateinit var parserRequestHandler: ParserRequestHandler

    @Value("\${stream.key}")
    private val streamKey: String = "mystream"

    @Value("\${groups.product}")
    private val groupId: String = "productcreated"

    @BeforeEach
    fun setUp() {
        redisTemplate = mock(RedisTemplate::class.java) as RedisTemplate<String, String>
        logger = mock(Logger::class.java)
        parserRequestHandler = ParserRequestHandler(redisTemplate, streamKey, groupId, logger)
    }

    @Test
    fun `test onMessage logs correct information`() {
        val executeRequest = ExecuteRequest("Kotlin", "println('Hello')", "execute", "[]")
        val record = ObjectRecord.create("mystream", executeRequest)

        parserRequestHandler.onMessage(record)

        verify(logger).info("Received action: execute")
    }
}
