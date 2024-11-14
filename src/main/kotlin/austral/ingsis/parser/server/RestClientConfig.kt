package austral.ingsis.parser.server

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class RestClientConfig {
    @Bean
    fun restTemplate(correlationIdInterceptor: CorrelationIdInterceptor): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.interceptors.add(correlationIdInterceptor)
        return restTemplate
    }
}
