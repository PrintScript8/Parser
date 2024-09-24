package printscript8.parser

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ParserApplication

fun main(args: Array<String>) {
	val dotenv = Dotenv.load()
	dotenv.entries().forEach { entry -> System.setProperty(entry.key, entry.value) }
	runApplication<ParserApplication>(*args)
}
