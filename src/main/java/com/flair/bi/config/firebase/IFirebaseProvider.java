package com.flair.bi.config.firebase;

import com.google.cloud.firestore.Firestore;

public interface IFirebaseProvider {
    Firestore getFirestore();
}
