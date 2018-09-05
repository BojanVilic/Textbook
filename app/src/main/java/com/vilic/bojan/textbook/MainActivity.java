package com.vilic.bojan.textbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private BottomNavigationView mainBottomNavigation;
    private ChatsFragment chatsFragment;
    private FriendsFragment friendsFragment;
    private ExploreFragment exploreFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mainBottomNavigation = (BottomNavigationView) findViewById(R.id.mainBottomNavigation);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            Intent authIntent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(authIntent);
            finish();
        }

        chatsFragment = new ChatsFragment();
        friendsFragment = new FriendsFragment();
        exploreFragment = new ExploreFragment();
        settingsFragment = new SettingsFragment();

        replaceFragment(chatsFragment);

        mainBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.chatMenu:
                        replaceFragment(chatsFragment);
                        return true;
                    case R.id.friendsMenu:
                        replaceFragment(friendsFragment);
                        return true;
                    case R.id.exploreMenu:
                        replaceFragment(exploreFragment);
                        return true;
                    case R.id.settingsMenu:
                        replaceFragment(settingsFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void replaceFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}

