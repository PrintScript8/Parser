package printscript8.parser.service

import org.springframework.stereotype.Service
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import printscript8.parser.model.TestModel
import printscript8.parser.repository.TestRepository

@Service
@RequiredArgsConstructor
@Slf4j
class TestService(private val testRepo: TestRepository) {
    fun saveTest(testModel: TestModel) {
        testRepo.save(testModel)
    }

    fun getTest(id: Int): TestModel {
        return testRepo.findById(id).orElseThrow()
    }
}