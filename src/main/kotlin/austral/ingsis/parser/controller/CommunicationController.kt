package austral.ingsis.parser.controller

import austral.ingsis.parser.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient

// import org.springframework.web.client.RestClient

@RestController
class CommunicationController(private val authService: AuthService) {
    val testClient: RestClient = RestClient.builder().baseUrl("http://localhost:8080").build()
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
        return authService.validateToken(token)
    }
}
