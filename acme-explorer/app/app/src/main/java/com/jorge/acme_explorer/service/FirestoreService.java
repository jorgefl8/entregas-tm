package com.jorge.acme_explorer.service;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;
import com.jorge.acme_explorer.entity.User;

import java.util.HashMap;
import java.util.Map;

public class FirestoreService {

    private static final String DATABASE_ID = "acme-tm";
    private static final String COLLECTION_USERS = "users";

    private static FirestoreService instance;
    private final FirebaseFirestore db;

    private FirestoreService() {
        this.db = FirebaseFirestore.getInstance(DATABASE_ID);
    }

    public static synchronized FirestoreService getInstance() {
        if (instance == null) {
            instance = new FirestoreService();
        }
        return instance;
    }

    public void ensureUserProfile(@NonNull FirebaseUser firebaseUser) {
        DocumentReference ref = db.collection(COLLECTION_USERS).document(firebaseUser.getUid());
        ref.get().addOnSuccessListener(snap -> {
            if (snap.exists()) return;
            User user = new User(
                    firebaseUser.getUid(),
                    splitFirst(firebaseUser.getDisplayName()),
                    splitRest(firebaseUser.getDisplayName()),
                    firebaseUser.getEmail(),
                    firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null,
                    System.currentTimeMillis()
            );
            ref.set(user);
        });
    }

    public ListenerRegistration listenUserProfile(@NonNull String uid,
                                                  EventListener<DocumentSnapshot> listener) {
        return db.collection(COLLECTION_USERS).document(uid).addSnapshotListener(listener);
    }

    public void updateUserProfile(@NonNull String uid, String name, String surname,
                                  OnCompleteListener<Void> onComplete) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("surname", surname);
        db.collection(COLLECTION_USERS).document(uid)
                .set(updates, SetOptions.merge())
                .addOnCompleteListener(onComplete);
    }

    public void updateUserPhotoUrl(@NonNull String uid, @NonNull String photoUrl,
                                   OnCompleteListener<Void> onComplete) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("photoUrl", photoUrl);
        db.collection(COLLECTION_USERS).document(uid)
                .set(updates, SetOptions.merge())
                .addOnCompleteListener(onComplete);
    }

    private static String splitFirst(String displayName) {
        if (displayName == null || displayName.isEmpty()) return "";
        int sp = displayName.indexOf(' ');
        return sp < 0 ? displayName : displayName.substring(0, sp);
    }

    private static String splitRest(String displayName) {
        if (displayName == null || displayName.isEmpty()) return "";
        int sp = displayName.indexOf(' ');
        return sp < 0 ? "" : displayName.substring(sp + 1);
    }
}
