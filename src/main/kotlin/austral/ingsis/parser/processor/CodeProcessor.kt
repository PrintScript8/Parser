package austral.ingsis.parser.processor

interface CodeProcessor {
    fun validate(code: String): Boolean

    fun execute(code: String): List<String>

    fun format(code: String): String

    fun analyze(
        code: String,
        config: String,
    ): List<error.Error>
}
