package com.flair.bi;

import com.flair.bi.config.TestFirebaseProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

    @Bean
    public TestFirebaseProvider firebaseProvider() {
        return new TestFirebaseProvider();
    }

}
