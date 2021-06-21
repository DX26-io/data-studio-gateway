package com.flair.bi.config.firebase;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class RealFirebaseProvider implements IFirebaseProvider {

    public Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

}
