package com.management.app.cash_management_back.karate;

import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AccountKarateTest {

    @LocalServerPort
    private int port;

    @BeforeAll
    static void beforeAll() {
    }

    @Karate.Test
    Karate runAccountFeature() {
        System.setProperty("baseUrl", "http://localhost:" + port);
        return Karate.run("classpath:features/account.feature");
    }
}