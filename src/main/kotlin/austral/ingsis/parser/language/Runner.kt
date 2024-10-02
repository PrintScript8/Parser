package austral.ingsis.parser.language

import austral.ingsis.parser.snippet.SnippetTranslator
import runner.Operations

class Runner(private val snippetTranslator: SnippetTranslator, private val version: String) {
    private val printScriptRunner: Operations = Operations(snippetTranslator.provideSnippet(), version)

    fun dummy() {
        printScriptRunner
    }
//    fun run() {
//        printScriptRunner.execute()
//    }
//
//    fun validate() {
//        printScriptRunner.validate()
//    }
//
//    fun analyze() {
//        printScriptRunner.analyze("")
//    }
}
