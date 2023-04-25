package com.example.jegyzetapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginBtn;
    ProgressBar progressBar;
    TextView createAccountBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginBtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progress_bar);
        createAccountBtnTextView = findViewById(R.id.create_account_text_view_btn);

        loginBtn.setOnClickListener(v -> loginUser());
        createAccountBtnTextView.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class)));
    }

    void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean isValidated = validateData(email, password);
        if (!isValidated) {
            return;
        }

        loginAccountInFirebase(email, password);
    }

    void loginAccountInFirebase(String email, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);

                if (task.isSuccessful()) {
                    // sikeres login
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        // go to mainactivity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }

                    else {
                        Utility.showToast(LoginActivity.this, "Az email cím nincs megerősítve!");
                    }
                }

                else {
                    Utility.showToast(LoginActivity.this, task.getException().getLocalizedMessage());
                }
            }
        });
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password) {
        // validate the data that are input by user
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Hibás email cím!");
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("A jelszó túl rövid!");
            return false;
        }
        return true;
    }
}