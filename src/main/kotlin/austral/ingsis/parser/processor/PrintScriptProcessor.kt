package austral.ingsis.parser.processor

import austral.ingsis.parser.exception.ProcessorException
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
        } catch (e: IllegalArgumentException) {
            throw ProcessorException("Validation failed for the given code", e)
        }
    }

    override fun execute(
        code: String,
        inputs: List<String>,
    ): List<String> {
        val runner = Operations(toInputStream(code), "1.1", inputs.iterator())

        return try {
            runner.execute().asSequence().toList()
        } catch (e: IllegalArgumentException) {
            throw ProcessorException("Execution failed", e)
        }
    }

    override fun format(
        code: String,
        json: String,
    ): String {
        val runner = Operations(toInputStream(code), "1.1", null)

        return try {
            runner.format(json)
        } catch (e: IllegalArgumentException) {
            throw ProcessorException("Formatting failed", e)
        }
    }

    override fun analyze(
        code: String,
        config: String,
    ): List<error.Error> {
        val runner = Operations(toInputStream(code), "1.1", null)

        return try {
            runner.analyze(config)
        } catch (e: IllegalArgumentException) {
            throw ProcessorException("Analysis failed", e)
        }
    }

    private fun toInputStream(code: String): InputStream {
        return ByteArrayInputStream(code.toByteArray(StandardCharsets.UTF_8))
    }
}
