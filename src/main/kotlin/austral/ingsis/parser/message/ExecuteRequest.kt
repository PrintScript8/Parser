package austral.ingsis.parser.message

data class ExecuteRequest(
    val language: String,
    val codeId: String,
    val action: String,
    val inputs: String,
)
