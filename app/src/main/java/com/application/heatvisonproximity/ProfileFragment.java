package com.application.heatvisonproximity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    String userPassword;
    TextView userName;
    EditText email, firstName, lastName;
    Button saveEdits, logOut;
    CircleImageView userProfile;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri setImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logOut = view.findViewById(R.id.btnLogout);
        saveEdits = view.findViewById(R.id.btnSaveChange);
        userName = view.findViewById(R.id.userName);
        email = view.findViewById(R.id.editUserEmail);
        firstName = view.findViewById(R.id.editFirstName);
        lastName = view.findViewById(R.id.editLastName);
        userProfile = view.findViewById(R.id.profileImage);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        DatabaseReference reference = database.getReference().child("user").child(mAuth.getUid());
        StorageReference storageReference = storage.getReference().child("upload").child(mAuth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userEmail = snapshot.child("email").getValue(String.class);
                String userPassword = snapshot.child("password").getValue(String.class);
                String userFirstName = snapshot.child("firstName").getValue(String.class);
                String userLastName = snapshot.child("lastName").getValue(String.class);
                String userProfilePic = snapshot.child("profilePic").getValue(String.class);

                if (userFirstName != null) {
                    firstName.setText(userFirstName);
                } else {
                    firstName.setText("N/A");
                }

                if (userLastName != null) {
                    lastName.setText(userLastName);
                } else {
                    lastName.setText("N/A");
                }

                if (userEmail != null) {
                    email.setText(userEmail);
                } else {
                    email.setText("N/A");
                }

                if (userProfilePic != null && !userProfilePic.isEmpty()) {
                    Picasso.get().load(userProfilePic).into(userProfile);
                } else {
                    userProfile.setImageResource(R.drawable.ic_user2); // Set a default image
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
            }
        });

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), 10);
            }
        });

        saveEdits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userFirstName = firstName.getText().toString();
                String userLastName = lastName.getText().toString();
                String userEmail = email.getText().toString();

                if (setImageUri != null) {
                    storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImageUri = uri.toString();
                                    Users users = new Users(mAuth.getUid(), userFirstName, userLastName, userEmail, userPassword, finalImageUri);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Data is Saved", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(getContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                } else {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImageUri = uri.toString();
                            Users users = new Users(mAuth.getUid(), userFirstName, userLastName, userEmail, userPassword, finalImageUri);
                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Data is Saved", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && data != null) {
            setImageUri = data.getData();
            userProfile.setImageURI(setImageUri);
        }
    }
}
