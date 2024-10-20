package austral.ingsis.parser.processor

import austral.ingsis.parser.exception.ValidationException
import runner.Operations
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

class PrintScriptProcessor : CodeProcessor {
    override fun validate(code: String): Boolean {
        val runner = Operations(toInputStream(code), "1.1", null)
        return try {
            runner.validate()
            true
        } catch (e: Exception) {
            throw ValidationException("Validation failed for the given code", e)
        }
    }

    override fun execute(code: String): List<String> {

        val runner = Operations(toInputStream(code), "1.1", null)

        return try {
            runner.execute().asSequence().toList()
        } catch (e: Exception) {
            throw e
        }

    }

    override fun format(code: String): String {
        val runner = Operations(toInputStream(code), "1.1", null)

        return try {
            runner.format()
        } catch (e: Exception) {
            throw e
        }
    }

    override fun analyze(
        code: String,
        config: String,
    ): List<error.Error> {
        val runner = Operations(toInputStream(code), "1.1", null)

        return try {
            runner.analyze(config)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun toInputStream(code: String): InputStream {
        return ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
    }
}
