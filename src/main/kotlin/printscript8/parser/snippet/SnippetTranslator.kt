package printscript8.parser.snippet

import java.io.InputStream

interface SnippetTranslator {
    fun provideSnippet(): InputStream
}