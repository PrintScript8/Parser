import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfig {

    @Value("\${DB_HOST}")
    lateinit var dbHost: String

    @Value("\${DB_PORT}")
    lateinit var dbPort: String

    @Value("\${DB_NAME}")
    lateinit var dbName: String

    @Value("\${DB_USER}")
    lateinit var dbUser: String

    @Value("\${DB_PASSWORD}")
    lateinit var dbPassword: String

    // You can use these variables to construct your data source URL or for other configurations
}