package austral.ingsis.parser.server

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.slf4j.MDC
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.http.server.reactive.MockServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class CorrelationIdFilterTest {
    private lateinit var filter: CorrelationIdFilter
    private lateinit var exchange: ServerWebExchange
    private lateinit var chain: WebFilterChain

    @BeforeEach
    fun setUp() {
        filter = CorrelationIdFilter()
        chain = mock(WebFilterChain::class.java)
        `when`(chain.filter(any())).thenReturn(Mono.empty())

        // Mock ServerWebExchange and its request and response
        exchange = mock(ServerWebExchange::class.java)
        val request =
            MockServerHttpRequest.get("/test")
                .header(CorrelationIdFilter.CORRELATION_ID_HEADER, "mock-correlation-id")
                .build()

        val response = MockServerHttpResponse()

        `when`(exchange.request).thenReturn(request)
        `when`(exchange.response).thenReturn(response)
    }

    @Test
    fun `should remove correlation ID from MDC after request`() {
        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete()

        assert(MDC.get(CorrelationIdFilter.CORRELATION_ID_KEY) == null)
    }
}
