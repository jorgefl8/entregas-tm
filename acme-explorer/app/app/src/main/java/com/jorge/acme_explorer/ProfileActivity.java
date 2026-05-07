package com.jorge.acme_explorer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.jorge.acme_explorer.entity.User;
import com.jorge.acme_explorer.service.FirebaseStorageService;
import com.jorge.acme_explorer.service.FirestoreService;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirestoreService firestore;
    private FirebaseStorageService storage;
    private ListenerRegistration listener;

    private TextView profileEmail;
    private ImageView profilePhoto;
    private Button profilePhotoButton;
    private ProgressBar profilePhotoProgress;
    private TextInputLayout profileName;
    private TextInputLayout profileSurname;
    private TextInputEditText profileNameEt;
    private TextInputEditText profileSurnameEt;
    private Button profileSaveButton;
    private Button profileLogoutButton;

    private boolean firstLoad = true;
    private String currentPhotoUrl;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickPhoto =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) uploadPhoto(uri);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirestoreService.getInstance();
        storage = FirebaseStorageService.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        profileEmail = findViewById(R.id.profileEmail);
        profilePhoto = findViewById(R.id.profilePhoto);
        profilePhotoButton = findViewById(R.id.profilePhotoButton);
        profilePhotoProgress = findViewById(R.id.profilePhotoProgress);
        profileName = findViewById(R.id.profileName);
        profileSurname = findViewById(R.id.profileSurname);
        profileNameEt = findViewById(R.id.profileNameEt);
        profileSurnameEt = findViewById(R.id.profileSurnameEt);
        profileSaveButton = findViewById(R.id.profileSaveButton);
        profileLogoutButton = findViewById(R.id.profileLogoutButton);
        ImageButton profileBackButton = findViewById(R.id.profileBackButton);

        profileEmail.setText(user.getEmail());

        profileBackButton.setOnClickListener(v -> finish());
        profileSaveButton.setOnClickListener(v -> save());
        profileLogoutButton.setOnClickListener(v -> logout());
        profilePhotoButton.setOnClickListener(v -> pickPhoto.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));

        listener = firestore.listenUserProfile(user.getUid(), this::onProfileUpdate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) listener.remove();
    }

    private void onProfileUpdate(@Nullable DocumentSnapshot snap, @Nullable Exception e) {
        if (e != null || snap == null || !snap.exists()) return;
        User user = snap.toObject(User.class);
        if (user == null) return;

        if (!firstLoad) {
            updatePhotoIfChanged(user.getPhotoUrl());
            return;
        }
        firstLoad = false;
        profileNameEt.setText(user.getName() != null ? user.getName() : "");
        profileSurnameEt.setText(user.getSurname() != null ? user.getSurname() : "");
        updatePhotoIfChanged(user.getPhotoUrl());
    }

    private void updatePhotoIfChanged(String newUrl) {
        if (newUrl == null || newUrl.equals(currentPhotoUrl)) return;
        currentPhotoUrl = newUrl;
        Glide.with(this)
                .load(newUrl)
                .placeholder(R.drawable.bg_avatar_placeholder)
                .error(R.drawable.bg_avatar_placeholder)
                .transform(new CircleCrop())
                .into(profilePhoto);
    }

    private void uploadPhoto(Uri uri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        profilePhotoButton.setEnabled(false);
        profilePhotoProgress.setVisibility(View.VISIBLE);

        storage.uploadProfilePhoto(user.getUid(), uri,
                downloadUrl -> firestore.updateUserPhotoUrl(user.getUid(), downloadUrl, task -> {
                    profilePhotoButton.setEnabled(true);
                    profilePhotoProgress.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Snackbar.make(profilePhotoButton, R.string.profile_photo_uploaded,
                                Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(profilePhotoButton, R.string.profile_photo_upload_error,
                                Snackbar.LENGTH_SHORT).show();
                    }
                }),
                error -> {
                    profilePhotoButton.setEnabled(true);
                    profilePhotoProgress.setVisibility(View.GONE);
                    Snackbar.make(profilePhotoButton, R.string.profile_photo_upload_error,
                            Snackbar.LENGTH_LONG).show();
                });
    }

    private void save() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String name = getText(profileNameEt);
        String surname = getText(profileSurnameEt);

        profileSaveButton.setEnabled(false);
        firestore.updateUserProfile(user.getUid(), name, surname, task -> {
            profileSaveButton.setEnabled(true);
            int msg = task.isSuccessful() ? R.string.profile_saved : R.string.profile_save_error;
            Snackbar.make(profileSaveButton, msg, Snackbar.LENGTH_SHORT).show();
        });
    }

    private void logout() {
        mAuth.signOut();
        // Sign out from Google as well so the account picker is shown next time.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, gso);
        client.signOut().addOnCompleteListener(t -> {
            Toast.makeText(this, R.string.profile_logged_out, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    private static String getText(@NonNull TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
