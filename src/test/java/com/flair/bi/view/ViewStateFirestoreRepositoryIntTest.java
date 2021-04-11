package com.flair.bi.view;

import com.flair.bi.AbstractContainerTestIT;
import com.flair.bi.TestDataGenerator;
import com.flair.bi.config.FirebaseTestConfig;
import com.flair.bi.config.firebase.IFirebaseProvider;
import com.flair.bi.domain.ViewState;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.Map;

import static com.flair.bi.view.ViewStateFirestoreRepository.COLLECTION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import(FirebaseTestConfig.class)
public class ViewStateFirestoreRepositoryIntTest extends AbstractContainerTestIT {

    @Autowired
    IViewStateRepository viewStateRepository;

    @Autowired
    IFirebaseProvider firebaseProvider;

    @Mock
    CollectionReference collectionReference;

    @Mock
    DocumentReference documentReference;

    @Mock
    DocumentSnapshot documentSnapshot;

    @Mock
    ApiFuture<DocumentSnapshot> apiFuture;

    @Before
    public void setUp() throws Exception {
        Firestore firestore = firebaseProvider.getFirestore();
        when(firestore.collection(eq(COLLECTION))).thenReturn(collectionReference);
        when(collectionReference.document(any())).thenReturn(documentReference);
        when(documentReference.get()).thenReturn(apiFuture);
        when(documentReference.set(any(Map.class))).thenReturn(apiFuture);
        when(apiFuture.get()).thenReturn(documentSnapshot);

    }

    @Test
    public void addViewState() {
        ViewState viewState = TestDataGenerator.createViewState();
        viewStateRepository.add(viewState);

        verify(collectionReference, atLeast(1))
                .document(viewState.getId() + ".tokensCount");

        for (int i = 0; i < 13; i++) {
            verify(collectionReference, atLeast(1))
                    .document(viewState.getId() + ".token." + i);
        }
    }

}