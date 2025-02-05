package com.edceo.springkeycloakdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class SpringKeycloakDemoApplicationTests {

    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> {
            // if context was successfully loaded, this block will be executed
        });
    }

}
