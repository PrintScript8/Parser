package printscript8.parser.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import printscript8.parser.service.TestService
import printscript8.parser.model.TestModel

@RestController
class HelloController(private val testService: TestService) {

    val testClient: RestClient = RestClient.builder().baseUrl("http://localhost:8080").build()

    @GetMapping("/hello")
    fun index(): ResponseEntity<String> {
        return ResponseEntity.ok("Greetings from Spring Boot!")
    }

    @PostMapping("/testAdd")
    fun post(@RequestBody test: TestModel): ResponseEntity<TestModel> {
        testService.saveTest(test)
        return ResponseEntity.ok(test)
    }

    @GetMapping("/testGet/{id}")
    fun get(@PathVariable id: Int): ResponseEntity<TestModel> {
        return ResponseEntity.ok(testService.getTest(id))
    }

    @GetMapping("/callEndpoint")
    fun callEndpoint(): ResponseEntity<String> {
        return ResponseEntity.ok(testClient.get().uri("/hello").retrieve().toEntity(String::class.java).body + "[REDIRECTED]")
    }
}