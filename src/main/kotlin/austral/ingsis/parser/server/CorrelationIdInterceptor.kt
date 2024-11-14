package austral.ingsis.parser.server

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CorrelationIdInterceptor : ClientHttpRequestInterceptor {
    var logger = LoggerFactory.getLogger(CorrelationIdInterceptor::class.java)

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        val correlationIdHeader = request.headers["X-Correlation-Id"]
        val correlationId =
            if (correlationIdHeader.isNullOrEmpty()) {
                UUID.randomUUID().toString()
            } else {
                correlationIdHeader.first()
            }
        request.headers.add("X-Correlation-Id", correlationId)

        logger.info("Method: ${request.method}, URI: ${request.uri}, CorrelationId: $correlationId")

        return execution.execute(request, body)
    }
}
