package austral.ingsis.parser

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["austral.ingsis.parser"])
class ParserApplication

fun main(args: Array<String>) {
//    val dotenv = Dotenv.load()
//    dotenv.entries().forEach { entry -> System.setProperty(entry.key, entry.value) }
    runApplication<ParserApplication>(args.toString())
}
