package com.example.tlotlotau.Auth;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar; // Import ActionBar
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tlotlotau.MainActivity;
import com.example.tlotlotau.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser; // Import FirebaseUser

public class ProfileActivity extends AppCompatActivity {
    ImageView profileImage;
    EditText displayName;
    Button btnUpdatePass,btnDeleteUser;
    FirebaseAuth mAuth;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth instance

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("User Profile");
        }
        // If actionBar is null, it means the theme might be NoActionBar.
        // Consider using a Toolbar in your layout and calling setSupportActionBar(toolbar) instead.

        profileImage = findViewById(R.id.profileImage);
        displayName = findViewById(R.id.displayName);
        btnUpdatePass = findViewById(R.id.btnUpdatePass);
        btnDeleteUser = findViewById(R.id.btn_delete_user);
        btnBack = findViewById(R.id.btnBack);

        // Set up the back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(getApplicationContext()).load(currentUser.getPhotoUrl()).into(profileImage);
            }
            displayName.setText(currentUser.getDisplayName());
        } else {
            // Handle user not being signed in, e.g., show a message or redirect.
            Toast.makeText(this, "User not signed in. Please login.", Toast.LENGTH_LONG).show();
            // Consider finishing the activity if user presence is mandatory
            // finish();
            // return; // To stop further execution in onCreate
        }

        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_password();
            }
        });
    }

    void update_password() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.update_password); // Ensure this layout exists

        EditText et_email = dialog.findViewById(R.id.et_email);
        EditText et_password = dialog.findViewById(R.id.et_password);
        EditText et_new_password = dialog.findViewById(R.id.et_new_password);
        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);
        ImageButton finish = dialog.findViewById(R.id.finish);

        // Set up the back button
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString().trim();
                String currentPassword = et_password.getText().toString();
                String newPassword = et_new_password.getText().toString();

                if (email.isEmpty()) {
                    et_email.setError("Please Enter your Email");
                    return;
                }
                if (currentPassword.isEmpty()) {
                    et_password.setError("Please Enter your Password");
                    return;
                }
                if (newPassword.isEmpty()) {
                    et_new_password.setError("Please Enter your New Password");
                    return;
                }
                if (currentPassword.equals(newPassword)) {
                    Toast.makeText(getApplicationContext(), "New Password cannot be the same as existing password", Toast.LENGTH_LONG).show();
                    return;
                }

                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(ProfileActivity.this, "User not authenticated. Please login again.", Toast.LENGTH_LONG).show();
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task1) {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Password Updated Successfully", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    } else {
                                        String errorMessage = "Failed to update password.";
                                        if (task1.getException() != null && task1.getException().getMessage() != null) {
                                            errorMessage = task1.getException().getMessage();
                                        } else if (task1.getException() != null) {
                                            errorMessage = task1.getException().toString(); // Fallback if getMessage is null
                                        }
                                        Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            String errorMessage = "Re-authentication failed.";
                            if (task.getException() != null && task.getException().getMessage() != null) {
                                errorMessage = task.getException().getMessage();
                            } else if (task.getException() != null) {
                                errorMessage = task.getException().toString(); // Fallback if getMessage is null
                            }
                            Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_user();
            }
        });


        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();

            }

        });
        dialog.show();

    }
    void delete_user()
    {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.delete_user);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, 2000);

        EditText  del_email = dialog.findViewById(R.id.del_email);
        EditText  del_password = dialog.findViewById(R.id.del_password);
        Button btn_delete_user = dialog.findViewById(R.id.btn_delete_user);
        ImageButton finish = dialog.findViewById(R.id.finish);

        // Set up the back button
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        btn_delete_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (del_email.getText().toString().isEmpty()) {
                    del_email.setError("Please Enter your Email");
                    return;
                } else if (del_password.getText().toString().isEmpty()) {
                    del_password.setError("Please Enter your Password");
                    return;

                } else {
                    AuthCredential credential =
                            EmailAuthProvider.getCredential(del_email.getText().toString(), del_password.getText().toString());
                    mAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(ProfileActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(ProfileActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

        dialog.show();


    }


}
