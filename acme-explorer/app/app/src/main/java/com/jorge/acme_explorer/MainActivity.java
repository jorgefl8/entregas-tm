package com.jorge.acme_explorer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.jorge.acme_explorer.service.FirestoreService;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001;

    private FirebaseAuth mAuth;
    private GoogleSignInClient googleClient;

    private TextInputLayout loginEmail;
    private TextInputLayout loginPass;
    private TextInputEditText loginEmailEt;
    private TextInputEditText loginPassEt;
    private Button loginButtonMail;
    private Button loginButtonGoogle;
    private Button loginButtonRegister;
    private ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.loginEmail);
        loginPass = findViewById(R.id.loginPass);
        loginEmailEt = findViewById(R.id.loginEmailEt);
        loginPassEt = findViewById(R.id.loginPassEt);
        loginButtonMail = findViewById(R.id.loginButtonMail);
        loginButtonGoogle = findViewById(R.id.loginButtonGoogle);
        loginButtonRegister = findViewById(R.id.loginButtonRegister);
        loginProgress = findViewById(R.id.loginProgress);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(this, gso);

        loginButtonMail.setOnClickListener(v -> attemptLoginMail());
        loginButtonGoogle.setOnClickListener(v -> attemptLoginGoogle());
        loginButtonRegister.setOnClickListener(v -> {
            Intent i = new Intent(this, SignUpActivity.class);
            i.putExtra(SignUpActivity.EXTRA_EMAIL, getText(loginEmailEt));
            startActivity(i);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            onLoginSuccess(user);
        }
    }

    private void attemptLoginMail() {
        loginEmail.setError(null);
        loginPass.setError(null);

        String email = getText(loginEmailEt);
        String pass = getText(loginPassEt);

        if (email.isEmpty()) {
            loginEmail.setError(getString(R.string.login_error_email_empty));
            return;
        }
        if (pass.isEmpty()) {
            loginPass.setError(getString(R.string.login_error_pass_empty));
            return;
        }

        showProgress(true);
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    showProgress(false);
                    if (!task.isSuccessful() || task.getResult() == null
                            || task.getResult().getUser() == null) {
                        Snackbar.make(loginButtonMail, R.string.login_error_invalid,
                                Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    FirebaseUser user = task.getResult().getUser();
                    if (!user.isEmailVerified()) {
                        showVerificationDialog(user);
                        return;
                    }
                    onLoginSuccess(user);
                });
    }

    private void attemptLoginGoogle() {
        startActivityForResult(googleClient.getSignInIntent(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != RC_SIGN_IN) return;

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account == null) {
                Snackbar.make(loginButtonGoogle, R.string.login_google_failed,
                        Snackbar.LENGTH_LONG).show();
                return;
            }
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Snackbar.make(loginButtonGoogle, R.string.login_google_failed,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        showProgress(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    showProgress(false);
                    if (!task.isSuccessful() || task.getResult() == null
                            || task.getResult().getUser() == null) {
                        Snackbar.make(loginButtonGoogle, R.string.login_google_failed,
                                Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    onLoginSuccess(task.getResult().getUser());
                });
    }

    private void showVerificationDialog(FirebaseUser user) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.login_error_unverified)
                .setPositiveButton(R.string.login_error_send_verification, (d, w) ->
                        user.sendEmailVerification().addOnCompleteListener(t -> {
                            int msg = t.isSuccessful()
                                    ? R.string.login_verification_sent
                                    : R.string.login_verification_failed;
                            Snackbar.make(loginButtonMail, msg, Snackbar.LENGTH_LONG).show();
                        }))
                .setNegativeButton(R.string.login_error_cancel, null)
                .show();
    }

    private void onLoginSuccess(FirebaseUser user) {
        FirestoreService.getInstance().ensureUserProfile(user);

        String name = user.getDisplayName();
        if (name == null || name.isEmpty()) name = user.getEmail();
        Toast.makeText(this, getString(R.string.login_success, name), Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, ProfileActivity.class));
        finish();
    }

    private void showProgress(boolean show) {
        loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        loginButtonMail.setEnabled(!show);
        loginButtonGoogle.setEnabled(!show);
        loginButtonRegister.setEnabled(!show);
    }

    private static String getText(@NonNull TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
