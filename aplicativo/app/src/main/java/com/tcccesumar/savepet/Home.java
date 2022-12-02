package com.tcccesumar.savepet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.tcccesumar.savepet.Services.Like.LikeFragment;
import com.tcccesumar.savepet.Main.Post.PostActivity;
import com.tcccesumar.savepet.Main.Profile.ProfileFragment;
import com.tcccesumar.savepet.Services.Search.SearchFragment;
import com.tcccesumar.savepet.home.HomeFragment;

public class Home extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navigationView = findViewById(R.id.savepet_bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
        String name = getIntent().getStringExtra("PAGE");
        if (name != null){
            loadfragment(new HomeFragment());

        }else{
            loadfragment(new HomeFragment());

        }

    }
    private boolean loadfragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.Home:
                fragment = new HomeFragment();
                break;

            case R.id.search:
                fragment = new SearchFragment();
                break;

            case R.id.post:
                fragment = null;
                startActivity(new Intent(Home.this, PostActivity.class));
                break;
            case R.id.likes:
                fragment = new LikeFragment();
                break;
            case R.id.profile:
                fragment = new ProfileFragment();
                break;
        }
        return loadfragment(fragment);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Você tem certeza que quer sair?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }


}