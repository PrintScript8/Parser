package austral.ingsis.parser.model

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class EmbeddedUserLanguage(
    val userId: Long,
    val language: String
) : Serializable {
    constructor() : this(0, "")
}