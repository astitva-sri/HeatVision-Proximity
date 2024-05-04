package com.application.heatvisonproximity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.heatvisonproximity.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "Alert Channel";
    private static final int NOTIFICATION_ID = 101;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference usersReference;

    ActivityMainBinding binding;
    boolean isImageUnMute = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        DatabaseReference reference = database.getReference().child("user");

        replaceFragment(new HomeFragment());
        binding.bottomNavBar.setBackground(null);

        binding.bottomNavBar.setOnItemSelectedListener(menuItem ->{

            int item = menuItem.getItemId();

            if (item == R.id.navHome){
                replaceFragment(new HomeFragment());
            } else if (item == R.id.navGallery) {
                replaceFragment(new GalleryFragment());
            } else if (item == R.id.navProfile) {
                replaceFragment(new ProfileFragment());
            }
            return true;

        });



        ImageView notificationButton = (ImageView) findViewById(R.id.notificationBell);
        notificationButton.setTooltipText("Notifications On/Off");

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImageUnMute){
                    notificationButton.setImageResource(R.drawable.mute_notification_bell);
                    Toast.makeText(MainActivity.this, "Push Notification Muted", Toast.LENGTH_SHORT).show();
                }else {
                    notificationButton.setImageResource(R.drawable.active_notification_bell);
                    Toast.makeText(MainActivity.this, "Push Notification Un-muted", Toast.LENGTH_SHORT).show();
                }isImageUnMute = !isImageUnMute;
            }
        });

        if (auth.getCurrentUser() == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private  void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}


//Notification command


/*
Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.app_icon, null);
    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
    Bitmap largeIcon = bitmapDrawable.getBitmap();

    NotificationManager nm= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    Notification notification = new Notification.Builder(this)
            .setLargeIcon(largeIcon)
            .setSmallIcon(R.drawable.active_notification_bell)
            .setContentText("Alert Message")
            .setSubText("Human was detected")
            .setChannelId(CHANNEL_ID)
            .build();
        nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "Detection Alert",NotificationManager.IMPORTANCE_HIGH));

                //This command will send alert notification when human detected
                nm.notify(NOTIFICATION_ID, notification);*/
