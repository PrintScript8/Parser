package austral.ingsis.parser.server

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.slf4j.Logger
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.http.server.reactive.MockServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class RequestLogFilterTest {
    private lateinit var filter: RequestLogFilter
    private lateinit var exchange: ServerWebExchange
    private lateinit var chain: WebFilterChain
    private lateinit var logger: Logger

    @BeforeEach
    fun setUp() {
        filter = RequestLogFilter()
        logger = mock(Logger::class.java)
        filter.logger = logger

        exchange = mock(ServerWebExchange::class.java)
        val request = MockServerHttpRequest.method(HttpMethod.GET, "/test-uri").build()
        val response = MockServerHttpResponse()
        response.setStatusCode(HttpStatus.OK)

        `when`(exchange.request).thenReturn(request)
        `when`(exchange.response).thenReturn(response)

        chain = mock(WebFilterChain::class.java)
        `when`(chain.filter(exchange)).thenReturn(Mono.empty())
    }

    @Test
    fun `should log request URI, method, and status code`() {
        val requestUri = "/test-uri"
        val method = "GET"
        val statusCode = HttpStatus.OK

        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete()

        val expectedLogMessage = "$method $requestUri - $statusCode"

        verify(logger).info(expectedLogMessage)
    }

    @Test
    fun `should continue filter chain`() {
        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete()

        verify(chain).filter(exchange)
    }
}
