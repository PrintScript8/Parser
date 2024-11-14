package austral.ingsis.parser.server

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.slf4j.Logger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse
import java.net.URI
import java.util.UUID

class CorrelationIdInterceptorTest {
    private lateinit var interceptor: CorrelationIdInterceptor
    private lateinit var request: HttpRequest
    private lateinit var execution: ClientHttpRequestExecution
    private lateinit var response: ClientHttpResponse

    @BeforeEach
    fun setUp() {
        interceptor = CorrelationIdInterceptor()
        request = mock(HttpRequest::class.java)
        execution = mock(ClientHttpRequestExecution::class.java)
        response = mock(ClientHttpResponse::class.java)

        `when`(execution.execute(any(HttpRequest::class.java), any(ByteArray::class.java))).thenReturn(response)
    }

    @Test
    fun `should add new correlation ID when missing`() {
        val headers = HttpHeaders()
        `when`(request.headers).thenReturn(headers)
        `when`(request.uri).thenReturn(URI.create("http://test-uri"))
        `when`(request.method).thenReturn(HttpMethod.GET)

        interceptor.intercept(request, ByteArray(0), execution)

        val correlationId = headers["X-Correlation-Id"]?.first()
        assert(correlationId != null && correlationId.isNotBlank()) { "Correlation ID should be generated and added" }
        verify(execution).execute(request, ByteArray(0))
    }

    @Test
    fun `should reuse existing correlation ID`() {
        val existingCorrelationId = UUID.randomUUID().toString()
        val headers = HttpHeaders()
        headers.add("X-Correlation-Id", existingCorrelationId)
        `when`(request.headers).thenReturn(headers)
        `when`(request.uri).thenReturn(URI.create("http://test-uri"))
        `when`(request.method).thenReturn(HttpMethod.GET)

        interceptor.intercept(request, ByteArray(0), execution)

        assert(headers["X-Correlation-Id"]?.first() == existingCorrelationId) {
            "Should reuse the existing correlation ID"
        }
        verify(execution).execute(request, ByteArray(0))
    }

    @Test
    fun `should log request with correlation ID`() {
        val existingCorrelationId = UUID.randomUUID().toString()
        val headers = HttpHeaders()
        headers.add("X-Correlation-Id", existingCorrelationId)
        `when`(request.headers).thenReturn(headers)
        `when`(request.uri).thenReturn(URI.create("http://test-uri"))
        `when`(request.method).thenReturn(HttpMethod.GET)

        // Capturing log output if needed
        val logger = mock(Logger::class.java)
        interceptor.logger = logger

        interceptor.intercept(request, ByteArray(0), execution)

        verify(logger).info("Method: GET, URI: http://test-uri, CorrelationId: $existingCorrelationId")
    }
}
