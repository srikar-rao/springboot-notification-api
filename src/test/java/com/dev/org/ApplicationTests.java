package com.dev.org;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Tag("integration")
@SpringBootTest(
        properties = {
            "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
            "spring.datasource.driver-class-name=org.h2.Driver",
            "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
            "spring.jpa.hibernate.ddl-auto=none"
        })
class ApplicationTests {

    @Test
    void contextLoads() {}
}
