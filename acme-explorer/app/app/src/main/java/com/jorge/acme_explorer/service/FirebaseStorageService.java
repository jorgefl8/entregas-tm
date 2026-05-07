package com.jorge.acme_explorer.service;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseStorageService {

    private static final String PATH_USERS = "users";
    private static final String FILE_PROFILE = "profile.jpg";

    private static FirebaseStorageService instance;
    private final FirebaseStorage storage;

    private FirebaseStorageService() {
        this.storage = FirebaseStorage.getInstance();
    }

    public static synchronized FirebaseStorageService getInstance() {
        if (instance == null) {
            instance = new FirebaseStorageService();
        }
        return instance;
    }

    public void uploadProfilePhoto(@NonNull String uid, @NonNull Uri imageUri,
                                   @NonNull OnSuccessListener<String> onSuccess,
                                   @NonNull OnFailureListener onFailure) {
        StorageReference ref = storage.getReference()
                .child(PATH_USERS).child(uid).child(FILE_PROFILE);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnap ->
                        ref.getDownloadUrl()
                                .addOnSuccessListener(url -> onSuccess.onSuccess(url.toString()))
                                .addOnFailureListener(onFailure))
                .addOnFailureListener(onFailure);
    }
}
