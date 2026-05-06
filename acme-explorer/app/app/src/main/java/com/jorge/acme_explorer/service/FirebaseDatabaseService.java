package com.jorge.acme_explorer.service;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseService {

    private static final String DATABASE_URL =
            "https://acme-tm-ff9f8-default-rtdb.europe-west1.firebasedatabase.app/";
    private static final String PATH_TRAVELS = "travels";

    private static FirebaseDatabaseService instance;
    private final FirebaseDatabase database;

    private FirebaseDatabaseService() {
        this.database = FirebaseDatabase.getInstance(DATABASE_URL);
        try {
            this.database.setPersistenceEnabled(true);
        } catch (DatabaseException ignored) {
            // setPersistenceEnabled throws if called more than once per instance.
        }
    }

    public static synchronized FirebaseDatabaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseDatabaseService();
        }
        return instance;
    }

    public DatabaseReference getTravelsRef() {
        return database.getReference(PATH_TRAVELS);
    }

    public DatabaseReference getTravelRef(@NonNull String travelId) {
        return database.getReference(PATH_TRAVELS).child(travelId);
    }
}
