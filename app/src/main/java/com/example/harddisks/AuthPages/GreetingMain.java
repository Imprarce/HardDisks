package com.example.harddisks.AuthPages;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.harddisks.MainPages.MainPage;
import com.example.harddisks.R;

public class GreetingMain extends AppCompatActivity implements AuthorizationFragment.OnAuthorizationSuccessListener {

    private NavController navController;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        navController = navHostFragment.getNavController();

    }

    // Тот самый метод, который мы реализуем через слушателя в авторизации, он плавно переключает активности
    @Override
    public void onAuthorizationSuccess() {
        Intent intent = new Intent(this, MainPage.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
