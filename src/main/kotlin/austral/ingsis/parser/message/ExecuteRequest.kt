package austral.ingsis.parser.message

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ExecuteRequest
    @JsonCreator
    constructor(
        @JsonProperty("ownerId") val ownerId: Long,
        @JsonProperty("language") val language: String,
        @JsonProperty("rules") val rules: String,
        @JsonProperty("action") val action: String,
        @JsonProperty("snippetId") val snippetId: Long?,
    )
