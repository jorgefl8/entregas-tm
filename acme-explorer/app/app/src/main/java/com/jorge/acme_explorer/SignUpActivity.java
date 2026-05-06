package com.jorge.acme_explorer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    public static final String EXTRA_EMAIL = "extra_email";

    private FirebaseAuth mAuth;

    private TextInputLayout signupEmail;
    private TextInputLayout signupPass;
    private TextInputLayout signupPassConfirmation;
    private TextInputEditText signupEmailEt;
    private TextInputEditText signupPassEt;
    private TextInputEditText signupPassConfirmationEt;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        signupEmail = findViewById(R.id.signupEmail);
        signupPass = findViewById(R.id.signupPass);
        signupPassConfirmation = findViewById(R.id.signupPassConfirmation);
        signupEmailEt = findViewById(R.id.signupEmailEt);
        signupPassEt = findViewById(R.id.signupPassEt);
        signupPassConfirmationEt = findViewById(R.id.signupPassConfirmationEt);
        signupButton = findViewById(R.id.signupButton);

        String prefilledEmail = getIntent().getStringExtra(EXTRA_EMAIL);
        if (prefilledEmail != null && !prefilledEmail.isEmpty()) {
            signupEmailEt.setText(prefilledEmail);
        }

        signupButton.setOnClickListener(v -> attemptSignUp());
    }

    private void attemptSignUp() {
        signupEmail.setError(null);
        signupPass.setError(null);
        signupPassConfirmation.setError(null);

        String email = getText(signupEmailEt);
        String pass = getText(signupPassEt);
        String confirmation = getText(signupPassConfirmationEt);

        if (email.isEmpty()) {
            signupEmail.setError(getString(R.string.signup_error_email_invalid));
            return;
        }
        if (pass.isEmpty()) {
            signupPass.setError(getString(R.string.signup_error_pass_invalid));
            return;
        }
        if (confirmation.isEmpty()) {
            signupPassConfirmation.setError(getString(R.string.signup_error_pass_invalid));
            return;
        }
        if (!pass.equals(confirmation)) {
            signupPass.setError(getString(R.string.signup_error_pass_mismatch));
            signupPassConfirmation.setError(getString(R.string.signup_error_pass_mismatch));
            return;
        }

        signupButton.setEnabled(false);
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    signupButton.setEnabled(true);
                    if (!task.isSuccessful() || task.getResult() == null
                            || task.getResult().getUser() == null) {
                        Snackbar.make(signupButton, R.string.signup_error_failed,
                                Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    FirebaseUser user = task.getResult().getUser();
                    user.sendEmailVerification();
                    Toast.makeText(this, R.string.signup_success, Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private static String getText(@NonNull TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
