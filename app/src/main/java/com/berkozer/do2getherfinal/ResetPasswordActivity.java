package com.berkozer.do2getherfinal;

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
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText userEmail;
    private Button resetButton;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        userEmail = (EditText) findViewById(R.id.email);
        resetButton = (Button) findViewById(R.id.reset_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Get Auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    userEmail.setError("Enter your registered email");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, "Email sent, check your inbox.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

}
