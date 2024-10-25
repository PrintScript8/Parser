package austral.ingsis.parser.message

import austral.ingsis.parser.processor.CodeProcessorFactory
import org.austral.ingsis.redis.RedisStreamConsumer
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.Duration

@Component
class ParserRequestHandler
    @Autowired
    constructor(
        redis: RedisTemplate<String, String>,
        @Value("\${stream.key}") streamKey: String,
        @Value("\${groups.product}") groupId: String,
        @Autowired private val restClientBuilder: RestClient.Builder
    ) : RedisStreamConsumer<ExecuteRequest>(streamKey, groupId, redis) {
    public override fun onMessage(record: ObjectRecord<String, ExecuteRequest>) {
        val processor = CodeProcessorFactory.getProcessor(record.value.language)
        val actionHandler = ActionHandler(processor, record, restClientBuilder)
        when (record.value.action) {
            "validate" -> actionHandler.handleValidate()
            "format" -> actionHandler.handleFormat()
            "execute" -> actionHandler.handleExecute()
            "analyze" -> actionHandler.handleAnalyze()
            else -> throw IllegalArgumentException("Invalid action")
        }
    }

    @Suppress("MagicNumber")
    override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, ExecuteRequest>> {
        return StreamReceiver.StreamReceiverOptions.builder()
            .pollTimeout(Duration.ofMillis(1000))
            .targetType(ExecuteRequest::class.java)
            .build()
    }
}