package austral.ingsis.parser.model

import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "user_rules")
class UserRules {
    @Id
    val id: EmbeddedUserLanguage = EmbeddedUserLanguage()
    val linterConfig: String = ""
    val parserConfig: String = ""
}