package austral.ingsis.parser.controller

import austral.ingsis.parser.server.CorrelationIdInterceptor
import austral.ingsis.parser.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient

// import org.springframework.web.client.RestClient

@RestController
class CommunicationController(private val authService: AuthService) {
    private val interceptor = CorrelationIdInterceptor()
    val testClient: RestClient =
        RestClient.builder()
            .requestInterceptor(interceptor)
            .baseUrl("http://localhost:8080")
            .build()
//
// //    @GetMapping("/testRequestMessage")
// //    fun callEndpoint(): ResponseEntity<String> {
// //        return ResponseEntity.ok(
// //            testClient.get().uri("/testRespondMessage").retrieve().toEntity(String::class.java).body,
// //        )
// //    }

    @GetMapping("/testRespondMessage")
    fun respondMessage(): ResponseEntity<String> {
        return ResponseEntity.ok("Greetings from Parser!")
    }

    @GetMapping("/testValidation")
    fun validateToken(
        @RequestHeader("Authorization", required = false) token: String,
    ): ResponseEntity<String> {
        val result = authService.validateToken(token)
        return if (result != null) {
            ResponseEntity.ok("Token is valid")
        } else {
            ResponseEntity.badRequest().body("Token is invalid")
        }
    }
}
