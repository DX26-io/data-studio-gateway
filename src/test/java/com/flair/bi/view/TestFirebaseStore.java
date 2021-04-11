package com.flair.bi.view;

import com.flair.bi.config.firebase.IFirebaseProvider;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.mockito.Mockito;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.flair.bi.view.ViewStateFirestoreRepository.COLLECTION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Component
public class TestFirebaseStore {
    private final Map<String, DocumentReference> documentReferencesMap = new ConcurrentHashMap<>();
    private final Map<String, Map> documentContentsMap = new ConcurrentHashMap<>();

    public TestFirebaseStore(IFirebaseProvider firebaseProvider) {
        Firestore firestore = firebaseProvider.getFirestore();
        CollectionReference collectionReference = Mockito.mock(CollectionReference.class);
        when(firestore.collection(eq(COLLECTION))).thenReturn(collectionReference);

        when(collectionReference.document(any())).thenAnswer(invocationOnMock -> {
            String path = invocationOnMock.getArgument(0, String.class);
            DocumentReference documentReference = Mockito.mock(DocumentReference.class);
            documentReferencesMap.put(path, documentReference);

            when(documentReference.set(any(Map.class))).thenAnswer(inv -> {
                Map arg = inv.getArgument(0, Map.class);
                documentContentsMap.put(path, arg);
                return Mockito.mock(ApiFuture.class);
            });

            when(documentReference.get()).thenAnswer(inv -> {
                ApiFuture future = Mockito.mock(ApiFuture.class);
                when(future.get()).thenAnswer(invocationOnMock1 -> {
                    DocumentSnapshot documentSnapshot = Mockito.mock(DocumentSnapshot.class);
                    when(documentSnapshot.getData()).thenReturn(documentContentsMap.get(path));
                    return documentSnapshot;
                });
                return future;
            });

            return documentReference;
        });
    }
}
