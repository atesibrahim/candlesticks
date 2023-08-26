package org.tr.candlesticks;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CandlesticksApplicationTests {

    @Test
    void contextLoads() {
        SpringApplication.run(CandlesticksApplication.class, "test");
    }

}
