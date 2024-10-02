package austral.ingsis.parser.snippet

import java.io.InputStream

interface SnippetTranslator {
    fun provideSnippet(): InputStream
}
