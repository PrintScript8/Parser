package austral.ingsis.parser.processor

interface CodeProcessor {
    fun validate(code: String): Boolean

    fun execute(
        code: String,
        inputs: List<String>,
    ): List<String>

    fun format(
        code: String,
        json: String,
    ): String

    fun analyze(
        code: String,
        config: String,
    ): List<error.Error>
}
