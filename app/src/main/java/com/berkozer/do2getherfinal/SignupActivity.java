package com.berkozer.do2getherfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private EditText userEmail, userPassword;
    private Button signIn, signUp, resetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Get auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        signIn = (Button) findViewById(R.id.sign_in_button);
        signUp = (Button) findViewById(R.id.sign_up_button);
        userEmail = (EditText) findViewById(R.id.email);
        userPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        resetPassword = (Button) findViewById(R.id.btn_reset_password);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = userEmail.getText().toString().trim();
                String password = userPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    userEmail.setError("Enter email address");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    userPassword.setError("Enter password");
                    return;
                }

                if (password.length() < 6) {
                    userPassword.setError("Password should be minimum 6 charachers");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Create user
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {
                                    userEmail.setError("Enter a valid email address");
                                } else {
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}