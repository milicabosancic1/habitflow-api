package com.habitflow.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev-h2")
class HabitFlowApiApplicationTests {

    @Test
    void contextLoads() {
        // Proverava da se Spring kontekst uspešno podiže (sa H2 bazom).
    }
}
