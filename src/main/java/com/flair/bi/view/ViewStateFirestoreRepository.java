package com.flair.bi.view;

import com.flair.bi.config.firebase.IFirebaseProvider;
import com.flair.bi.config.jackson.JacksonUtil;
import com.flair.bi.domain.ViewState;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
public class ViewStateFirestoreRepository implements IViewStateRepository {

    public static final int CHUNK_SIZE = 100_000;
    public static final String COLLECTION = "view-state";

    private final int chunkSize;
    private final Firestore db;
    private final ExecutorService executorService = Executors.newWorkStealingPool();
    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public ViewStateFirestoreRepository(IFirebaseProvider firebaseProvider, int chunkSize) {
        this.db = firebaseProvider.getFirestore();
        this.chunkSize = chunkSize;
    }

    @Override
    public void add(ViewState viewState) {
        log.info("Create view state {}", viewState.getId());
        if (viewState.getId() == null) {
            viewState.setId(UUID.randomUUID().toString());
        }

        lock(viewState.getId(), () -> {
            remove(viewState);

            String strData = JacksonUtil.toString(viewState);
            List<String> tokens = Lists.newArrayList(Splitter.fixedLength(chunkSize).split(strData));

            List<Future<?>> futures = new ArrayList<>();

            futures.add(saveDocumentValue(viewState, "tokensCount", tokens.size()));

            for (int i = 0; i < tokens.size(); i++) {
                String t = tokens.get(i);
                futures.add(saveDocumentValue(viewState, "token." + i, t));
            }

            waitAllFutures(futures);
        });
    }

    private ApiFuture<WriteResult> saveDocumentList(ViewState viewState, String subkey, List<?> list) throws RuntimeException {
        log.debug("saving document {} key {}", viewState.getId(), subkey);
        DocumentReference docRef = db.collection(COLLECTION).document(viewState.getId() + "." + subkey);
        HashMap<String, Object> map = new HashMap<>();
        map.put("list", list);
        return docRef.set(map);
    }

    private ApiFuture<WriteResult> saveDocumentValue(ViewState viewState, String subkey, Object value) throws RuntimeException {
        return saveDocumentList(viewState, subkey, Arrays.asList(value));
    }

    @SneakyThrows
    private List<?> getDocumentList(String documentId, String subkey) throws RuntimeException {
        CollectionReference collection = db.collection(COLLECTION);
        DocumentReference docRef = collection.document(documentId + "." + subkey);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot snapshot = future.get();
        Map<String, Object> map = snapshot.getData();
        if (map == null || map.isEmpty()) {
            return null;
        }
        String str = JacksonUtil.toString(map.get("list"));
        return JacksonUtil.fromString(str, List.class);
    }

    private <T> T getDocumentValue(String documentId, String subkey) throws RuntimeException {
        List<?> list = getDocumentList(documentId, subkey);
        if (list == null || list.isEmpty()) {
            return null;
        }
        T value = (T) list.get(0);
        log.debug("reading document {} key {} = {}", documentId, subkey, shrinkValue(String.valueOf(value)));
        return value;
    }

    private String shrinkValue(String str) {
        int printSize = 50;
        return str.substring(0, Math.min(printSize, str.length())) + "...[" + str.length() + "]..." + str.substring(Math.max(0, str.length() - printSize));
    }

    @Override
    public void update(ViewState viewState) {
        add(viewState);
    }

    @Override
    public void remove(ViewState viewState) {
        String vId = viewState.getId();

        log.info("Remove view state {}", vId);

        lock(vId, () -> {
            Integer tokensCount = getDocumentValue(vId, "tokensCount");
            if (tokensCount == null) {
                return;
            }

            List<Future<?>> futures = new ArrayList<>();
            for (long i = 0; i < tokensCount; i++) {
                futures.add(deleteDocument(vId + ".token." + i));
            }

            futures.add(deleteDocument(vId + ".tokensCount"));

            waitAllFutures(futures);
        });
    }

    private ApiFuture<WriteResult> deleteDocument(String documentName) {
        log.debug("Deleting document {}", documentName);
        DocumentReference docRef = db.collection(COLLECTION).document(documentName);
        return docRef.delete();
    }

    @Override
    public ViewState get(String s) {
        log.info("Get view state {}", s);

        return lock(s, () -> {
            Integer tokensCount = getDocumentValue(s, "tokensCount");
            if (tokensCount == null) {
                return null;
            }

            List<Future<String>> futures = new ArrayList<>();
            for (int i = 0; i < tokensCount; i++) {
                int finalI = i;
                futures.add(
                        executorService.submit(() -> getDocumentValue(s, "token." + finalI))
                );
            }
            String str = futures.stream()
                    .map(f -> doUnchecked(() -> f.get()))
                    .collect(Collectors.joining());

            return JacksonUtil.fromString(str, ViewState.class);
        });
    }

    @Override
    public ViewState get(String s, String s1) {
        throw new NotImplementedException("get not implemented " + s + " " + s1);
    }

    @Override
    public List<ViewState> getAll() {
        throw new NotImplementedException("getAll not implemented");
    }

    @Override
    public boolean contains(String s) {
        throw new NotImplementedException("contains not implemented");
    }

    private static <T> T doUnchecked(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private <T> T lock(String lockId, Callable<T> runnable) {
        ReentrantLock lock = getLock(lockId);
        lock.lock();
        try {
            return runnable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    private void lock(String lockId, Runnable runnable) {
        ReentrantLock lock = getLock(lockId);
        lock.lock();
        try {
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    private ReentrantLock getLock(String vId) {
        return locks.computeIfAbsent(vId, (name) -> new ReentrantLock());
    }

    private void waitAllFutures(List<Future<?>> futures) {
        futures.forEach(f -> doUnchecked(() -> f.get()));
    }
}
