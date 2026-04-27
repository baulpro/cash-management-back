package com.management.app.cash_management_back.karate;

import com.intuit.karate.junit5.Karate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TransactionKarateTest {

    @LocalServerPort
    private int port;

    @Karate.Test
    Karate runTransactionFeature() {
        System.setProperty("baseUrl", "http://localhost:" + port);
        return Karate.run("classpath:features/transaction.feature");
    }
}