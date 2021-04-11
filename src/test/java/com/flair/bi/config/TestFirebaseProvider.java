package com.flair.bi.config;

import com.flair.bi.config.firebase.IFirebaseProvider;
import com.google.cloud.firestore.Firestore;
import org.mockito.Mockito;

public class TestFirebaseProvider implements IFirebaseProvider {

    private Firestore mock = Mockito.mock(Firestore.class);

    @Override
    public Firestore getFirestore() {
        return mock;
    }

}
