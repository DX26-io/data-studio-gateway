package com.flair.bi.view;

import com.flair.bi.AbstractContainerTestIT;
import com.flair.bi.TestDataGenerator;
import com.flair.bi.config.FirebaseTestConfig;
import com.flair.bi.domain.ViewState;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static org.junit.Assert.assertEquals;

@Import(FirebaseTestConfig.class)
public class ViewStateFirestoreRepositoryIntTest extends AbstractContainerTestIT {

    @Autowired
    IViewStateRepository viewStateRepository;

    @Autowired
    TestFirebaseStore store;

    @Before
    public void setUp() {
    }

    @Test
    public void shouldReturnSameViewStateThatWasSaved() {
        ViewState viewState = TestDataGenerator.createViewState();
        viewStateRepository.add(viewState);

        ViewState loadedViewState = viewStateRepository.get(viewState.getId());
        assertEquals(viewState, loadedViewState);
    }

}