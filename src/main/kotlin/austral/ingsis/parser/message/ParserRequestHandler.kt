package austral.ingsis.parser.message

import austral.ingsis.parser.service.AuthService
import austral.ingsis.parser.service.SnippetService
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.LogManager
import org.austral.ingsis.redis.RedisStreamConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.nio.file.AccessDeniedException
import java.time.Duration

@Component
class ParserRequestHandler
    @Autowired
    constructor(
        redis: RedisTemplate<String, String>,
        @Value("\${stream.key}") streamKey: String,
        @Value("\${groups.product}") groupId: String,
        @Autowired private val restClientBuilder: RestClient.Builder,
        @Autowired private val authService: AuthService,
    ) : RedisStreamConsumer<String>(streamKey, groupId, redis) {
        private val permissionsClient = restClientBuilder.baseUrl("http://permission-service:8080").build()
        val logger = LogManager.getLogger(ParserRequestHandler::class.java)

        public override fun onMessage(record: ObjectRecord<String, String>) {
            val objectMapper = ObjectMapper()
            val jsonMessage = record.value
            logger.info("Received message: $jsonMessage")
            val executeRequest = objectMapper.readValue(jsonMessage, ExecuteRequest::class.java)
            logger.info("Received message: $executeRequest")
            val snippets = getSnippets(executeRequest.token)
            val actionHandler = ActionHandler(SnippetService(restClientBuilder), restClientBuilder)
            val ownerId = getIdByToken(executeRequest.token)
            when (executeRequest.action) {
                "validate" -> actionHandler.handleValidate(snippets, executeRequest.language)
                "format" ->
                    actionHandler.handleFormat(
                        snippets,
                        executeRequest.language,
                        executeRequest.rules,
                        executeRequest.token,
                    )
                "execute" ->
                    actionHandler.handleExecute(
                        executeRequest.language,
                        executeRequest.snippetId,
                        executeRequest.token,
                    )
                "lint" ->
                    actionHandler.handleAnalyze(
                        snippets,
                        executeRequest.language,
                        executeRequest.rules,
                        executeRequest.token,
                    )
                else -> throw IllegalArgumentException("Invalid action")
            }
        }

        @Suppress("MagicNumber")
        override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> {
            return StreamReceiver.StreamReceiverOptions.builder()
                .pollTimeout(Duration.ofMillis(1000))
                .targetType(String::class.java) // Use String type here
                .build()
        }

        private fun getIdByToken(token: String): String {
            val id: String? = authService.validateToken(token)
            if (id != null) {
                return id
            }
            // error, not authenticated
            throw AccessDeniedException("Could not validate user by it's token")
        }

        fun getSnippets(ownerId: String): List<Long> {
            val response =
                permissionsClient.get()
                    .uri("/users/snippets/{id}", ownerId)
                    .headers { headers -> headers.set("id", ownerId.toString()) }
                    .retrieve()
                    .toEntity(List::class.java)
            logger.info("Received snippets: ${response.body}")
            return response.body as List<Long>
        }
    }
