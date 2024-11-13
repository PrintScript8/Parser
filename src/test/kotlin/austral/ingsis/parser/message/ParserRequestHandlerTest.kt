package austral.ingsis.parser.message

import austral.ingsis.parser.service.AuthService
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.apache.logging.log4j.Logger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.web.client.RestClient

class ParserRequestHandlerTest {
    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var restClientBuilder: RestClient.Builder
    private lateinit var restClient: RestClient
    private lateinit var parserRequestHandler: ParserRequestHandler
    private lateinit var logger: Logger

    @BeforeEach
    fun setUp() {
        redisTemplate = mockk()
        restClientBuilder = mockk()
        restClient = mockk()
        logger = mockk(relaxed = true)
        val authService = mockk<AuthService>()

        every { restClientBuilder.baseUrl(any()) } returns restClientBuilder
        every { restClientBuilder.build() } returns restClient
        every { authService.validateToken(any()) } returns "testToken"

        parserRequestHandler =
            spyk(ParserRequestHandler(redisTemplate, "streamKey", "groupId", restClientBuilder)) {
                every { this@spyk.logger } returns this@ParserRequestHandlerTest.logger
            }
    }

    @Test
    fun `test onMessage with validate action`() {
        val record = mockk<ObjectRecord<String, String>>()
        val objectMapper = ObjectMapper()
        val executeRequest = ExecuteRequest("testToken", "printscript", "{}", "validate", 1L)
        val jsonMessage = objectMapper.writeValueAsString(executeRequest)

        every { parserRequestHandler.getSnippets(executeRequest.token) } returns emptyList()

        every { record.value } returns jsonMessage

        parserRequestHandler.onMessage(record)

        verify { logger.info("Received message: $jsonMessage") }
        verify { logger.info("Received message: $executeRequest") }
    }

    @Test
    fun `test onMessage with format action`() {
        val record = mockk<ObjectRecord<String, String>>()
        val objectMapper = ObjectMapper()
        val config =
            "{\n" +
                "    \"rules\": {\n" +
                "        \"spaceBeforeColon\": false,\n" +
                "        \"spaceAfterColon\": false,\n" +
                "        \"spaceAroundEquals\": false,\n" +
                "        \"newlineBeforePrintln\": 1\n" +
                "    }\n" +
                "}"
        val executeRequest = ExecuteRequest("testToken", "printscript", config, "format", 1L)
        val jsonMessage = objectMapper.writeValueAsString(executeRequest)

        every { parserRequestHandler.getSnippets(executeRequest.token) } returns emptyList()

        every { record.value } returns jsonMessage

        parserRequestHandler.onMessage(record)

        verify { logger.info("Received message: $jsonMessage") }
        verify { logger.info("Received message: $executeRequest") }
    }

    @Test
    fun `test onMessage with lint action`() {
        val record = mockk<ObjectRecord<String, String>>()
        val objectMapper = ObjectMapper()
        val executeRequest = ExecuteRequest("testToken", "printscript", "{}", "lint", 1L)
        val jsonMessage = objectMapper.writeValueAsString(executeRequest)

        every { parserRequestHandler.getSnippets(executeRequest.token) } returns emptyList()

        every { record.value } returns jsonMessage

        parserRequestHandler.onMessage(record)

        verify { logger.info("Received message: $jsonMessage") }
        verify { logger.info("Received message: $executeRequest") }
    }
}
