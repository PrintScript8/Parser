package austral.ingsis.parser.message

import org.austral.ingsis.redis.RedisStreamConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ParserRequestHandler
    @Autowired
    constructor(
        redis: RedisTemplate<String, String>,
        @Value("\${stream.key}") streamKey: String,
        @Value("\${groups.product}") groupId: String,
    ) : RedisStreamConsumer<ExecuteRequest>(streamKey, groupId, redis) {
        private val logger: Logger = LoggerFactory.getLogger(ParserRequestHandler::class.java)

        override fun onMessage(record: ObjectRecord<String, ExecuteRequest>) {
            logger.info("Received action: ${record.value.action}")
        }

        @Suppress("MagicNumber")
        override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, ExecuteRequest>> {
            return StreamReceiver.StreamReceiverOptions.builder()
                .pollTimeout(Duration.ofMillis(1000))
                .targetType(ExecuteRequest::class.java)
                .build()
        }
    }
