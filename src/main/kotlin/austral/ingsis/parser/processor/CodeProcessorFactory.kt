package austral.ingsis.parser.processor

object CodeProcessorFactory {
    fun getProcessor(language: String): CodeProcessor {
        return when (language.lowercase()) {
            "printscript" -> PrintScriptProcessor()
            else -> throw IllegalArgumentException("Unsupported language: $language")
        }
    }
}
