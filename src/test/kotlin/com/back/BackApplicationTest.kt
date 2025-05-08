package com.back

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test") // application-test.yml 활성화
class BackApplicationTest {

    @Test
    fun contextLoads() {
    }

}
