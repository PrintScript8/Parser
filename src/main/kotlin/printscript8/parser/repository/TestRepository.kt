package printscript8.parser.repository

import org.springframework.data.jpa.repository.JpaRepository
import printscript8.parser.model.TestModel

interface TestRepository : JpaRepository<TestModel, Int> {
}