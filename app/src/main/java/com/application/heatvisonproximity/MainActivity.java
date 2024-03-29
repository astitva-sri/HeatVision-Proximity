package com.application.heatvisonproximity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.application.heatvisonproximity.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        ImageView fab = (ImageView) findViewById(R.id.notificationBell);
        fab.setTooltipText("Notifications On/Off");
    }

    private  void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}