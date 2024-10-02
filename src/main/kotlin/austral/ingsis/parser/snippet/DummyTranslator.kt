package austral.ingsis.parser.snippet

import java.io.InputStream

class DummyTranslator : SnippetTranslator {
    override fun provideSnippet(): InputStream {
        return InputStream.nullInputStream()
    }
}
