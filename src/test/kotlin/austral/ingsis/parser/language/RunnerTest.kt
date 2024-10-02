package austral.ingsis.parser.language

import austral.ingsis.parser.snippet.DummyTranslator
import org.junit.jupiter.api.Test

class RunnerTest {
    @Test
    fun run() {
        val runner = Runner(DummyTranslator(), "1.1")
        runner.dummy()
//        runner.run()
//        runner.analyze()
//        runner.validate()
    }
}
