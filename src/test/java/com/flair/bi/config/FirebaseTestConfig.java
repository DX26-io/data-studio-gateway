package com.flair.bi.config;

import com.flair.bi.config.firebase.IFirebaseProvider;
import com.flair.bi.view.IViewStateRepository;
import com.flair.bi.view.ViewStateFirestoreRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class FirebaseTestConfig {

    @Primary
    @Bean
    public IViewStateRepository testViewStateRepository(IFirebaseProvider firebaseProvider) {
        return new ViewStateFirestoreRepository(firebaseProvider, 50);
    }

}
