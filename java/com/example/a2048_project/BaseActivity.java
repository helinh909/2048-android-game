package com.example.a2048_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

//All the acitivty that is shown extends this activity
public abstract class BaseActivity extends AppCompatActivity {

    private int currentTheme = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTheme(); // we apply the theme of the application
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        int savedTheme = prefs.getInt("app_theme", 0);
        //if the theme = -1 we load the theme that is saved
        if (currentTheme == -1) {
            currentTheme = savedTheme;
        } else if (currentTheme != savedTheme) {
            recreate();
        }

        //if we are not in 2048 we start the menuy music
        if (!(this instanceof Game2048)) {
            if (!MusicManager.isPlaying()) {
                MusicManager.startMenuMusic(this);
            }
        }
    }


    private void applyTheme() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        int theme = prefs.getInt("app_theme", 0);

        //we apply the different theme
        switch (theme) {
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                setTheme(R.style.AppTheme_Dark);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                setTheme(R.style.AppTheme_Colorful);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                setTheme(R.style.AppTheme_Light);
                break;
        }

        currentTheme = theme;
    }
}
