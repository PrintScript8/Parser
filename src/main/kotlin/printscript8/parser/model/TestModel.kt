package printscript8.parser.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.AllArgsConstructor
import lombok.Data

@AllArgsConstructor
@Data
@Entity
@Table(name = "test")
class TestModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val name: String
) {
    constructor() : this(0, "")
}