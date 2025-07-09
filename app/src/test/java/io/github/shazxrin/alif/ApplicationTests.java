package io.github.shazxrin.alif;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
class ApplicationTests {
    @Container
    static final ComposeContainer environment = new ComposeContainer(new File("../deploy/dev.compose.yaml"));

    @Test
    void contextLoads() {
    }

}
