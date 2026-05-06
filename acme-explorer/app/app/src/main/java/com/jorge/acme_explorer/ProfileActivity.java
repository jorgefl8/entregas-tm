package com.jorge.acme_explorer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.jorge.acme_explorer.service.FirestoreService;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirestoreService firestore;
    private ListenerRegistration listener;

    private TextView profileEmail;
    private TextInputLayout profileName;
    private TextInputLayout profileSurname;
    private TextInputEditText profileNameEt;
    private TextInputEditText profileSurnameEt;
    private Button profileSaveButton;
    private Button profileLogoutButton;

    private boolean firstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirestoreService.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        profileEmail = findViewById(R.id.profileEmail);
        profileName = findViewById(R.id.profileName);
        profileSurname = findViewById(R.id.profileSurname);
        profileNameEt = findViewById(R.id.profileNameEt);
        profileSurnameEt = findViewById(R.id.profileSurnameEt);
        profileSaveButton = findViewById(R.id.profileSaveButton);
        profileLogoutButton = findViewById(R.id.profileLogoutButton);

        profileEmail.setText(user.getEmail());

        profileSaveButton.setOnClickListener(v -> save());
        profileLogoutButton.setOnClickListener(v -> logout());

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

        // Only populate fields on first load to avoid overwriting in-progress edits
        // when a remote change arrives while the user is typing.
        if (!firstLoad) return;
        firstLoad = false;
        profileNameEt.setText(user.getName() != null ? user.getName() : "");
        profileSurnameEt.setText(user.getSurname() != null ? user.getSurname() : "");
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
