package com.application.heatvisonproximity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextFirstName,editTextLastName, editTextConfirmPassword;
    Button btnLogin, btnGetSignup;
    android.app.ProgressDialog progressDialog;
    CircleImageView btnAddProfile;
    FirebaseAuth mAuth;
    Uri imageURI;
    String imageuri;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseDatabase database;
    FirebaseStorage storage;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Account data being established");
        progressDialog.setCancelable(false);

        database =FirebaseDatabase.getInstance();
        storage =FirebaseStorage.getInstance();

        mAuth = FirebaseAuth.getInstance();

        editTextFirstName = findViewById(R.id.getFirstName);
        editTextLastName = findViewById(R.id.getLastName);
        editTextEmail = findViewById(R.id.getEmail);
        editTextPassword = findViewById(R.id.getPassword);
        editTextConfirmPassword = findViewById(R.id.getConfirmPassword);
        btnAddProfile = findViewById(R.id.profile_image);
        btnGetSignup = findViewById(R.id.btnSignUp);



        /*Save the register data*/
        btnGetSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password,confirmPassword, firstName,lastName;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                firstName = String.valueOf(editTextFirstName.getText());
                lastName = String.valueOf(editTextLastName.getText());
                confirmPassword = String.valueOf(editTextConfirmPassword.getText());

                if (TextUtils.isEmpty(email)){
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(firstName)){
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Please enter first name", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(lastName)){
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Please enter last name", Toast.LENGTH_SHORT).show();
                    return;
                }if (TextUtils.isEmpty(confirmPassword)){
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Please renter your password", Toast.LENGTH_SHORT).show();
                    return;
                }if (!email.matches(emailPattern)){
                    progressDialog.dismiss();
                    editTextEmail.setError("Type a valid email here");
                    return;
                }if (password.length() < 6){
                    progressDialog.dismiss();
                    editTextPassword.setError("Password must be more than 6 characters");
                    Toast.makeText(SignupActivity.this, "Password must have more than 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }if (!password.equals(confirmPassword)){
                    progressDialog.dismiss();
                    editTextConfirmPassword.setError("Passwords doesn't match");
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
/*
                                    FirebaseUser user = mAuth.getCurrentUser();
*/
                            Toast.makeText(SignupActivity.this, "Account Created",Toast.LENGTH_SHORT).show();

                            String id = task.getResult().getUser().getUid();
                            DatabaseReference reference = database.getReference().child("user").child(id);
                            StorageReference storageReference = storage.getReference().child("Upload").child(id);

                            if (imageURI != null){
                                storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()){
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    imageuri = uri.toString();
                                                    Users users = new Users(id, firstName, email, password, imageuri, lastName);
                                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                progressDialog.show();
                                                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }else {
                                                                Toast.makeText(SignupActivity.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            }else {
                                imageuri = "https://firebasestorage.googleapis.com/v0/b/heatvison-proximity.appspot.com/o/stry2.jpg?alt=media&token=f77b62a1-a3a4-4880-bb66-c90d73b7548e";
                                Users users = new Users(id, firstName, email, password, imageuri, lastName);
                                reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            progressDialog.show();
                                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Toast.makeText(SignupActivity.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Signup authentication failed.",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        /*Button to set Profile image of the user*/
        btnAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),10);
            }
        });


        /*Button to get redirected to login page*/
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10){
            if (data != null){
                imageURI = data.getData();
                btnAddProfile.setImageURI(imageURI);
            }
        }
    }
}