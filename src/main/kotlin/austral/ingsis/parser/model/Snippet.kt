package austral.ingsis.parser.model

data class Snippet(
    val id: Long,
    val name: String,
    val description: String,
    var code: String,
    val language: String,
    val ownerId: Long,
)
