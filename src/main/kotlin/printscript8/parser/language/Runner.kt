package printscript8.parser.language

import printscript8.parser.snippet.SnippetTranslator
import runner.Operations

class Runner(private val snippetTranslator: SnippetTranslator, private val version: String) {
    private val runner: Operations = Operations(snippetTranslator.provideSnippet(),version)

    fun run() {
        runner.execute()
    }

    fun validate() {
        runner.validate()
    }

    fun analyze() {
        runner.analyze("")
    }

}