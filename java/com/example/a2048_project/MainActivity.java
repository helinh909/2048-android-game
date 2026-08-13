package com.example.a2048_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //fit the activity to the screen
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //button to start a game
        Button playButton = findViewById(R.id.button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TutorialOneActivity.class);
                startActivity(intent);

                //if we click for the first time on start game the tutorial is start else we start the game
                SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                boolean isFirstStart = prefs.getBoolean("firstStart", true);

                if (isFirstStart) {
                    startActivity(new Intent(MainActivity.this, TutorialOneActivity.class));
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("firstStart", false);
                    editor.apply();
                    finish();
                } else {
                    SharedPreferences gamePrefs = getSharedPreferences("GameData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = gamePrefs.edit();
                    editor.putBoolean("challengeMode", false);
                    editor.putBoolean("savedGameIsChallenge", false);
                    editor.apply();

                    intent = new Intent(MainActivity.this, Game2048.class);
                    startActivity(intent);
                }
            }
        });


        //button to resume the game
        Button resumeButton = findViewById(R.id.button1);
        resumeButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
            String lastDateWon = prefs.getString("lastDailyChallengeWon", "N/A");
            String currentDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
            boolean isSavedGameChallenge = prefs.getBoolean("savedGameIsChallenge", false);
            boolean hasSavedGame = prefs.contains("currentScore");

            LinearLayout mainLayout = findViewById(R.id.main);

            //if no game was launch before
            if (!hasSavedGame) {
                Snackbar snackbar = Snackbar
                        .make(mainLayout, "Aucune partie en cours", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                snackbar.show();
//            }
            } else {
                //daily not won or normal game
                Intent intent = new Intent(MainActivity.this, Game2048.class);
                intent.putExtra("resumeGame", true);
                startActivity(intent);
            }
        });

        //button for the option
        Button optionButton = findViewById(R.id.btnOption);
        optionButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, OptionActivity.class);
            startActivity(intent);
        });

        //button for the highScore
        Button highScoresButton = findViewById(R.id.highScoresButton);
        highScoresButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HighScoreActivity.class);
            startActivity(intent);
        });


        //button to go to the dailyChallenge
        Button dailyChallengeButton = findViewById(R.id.btnChallenge);
        dailyChallengeButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
            String lastDateWon = prefs.getString("lastDailyChallengeWon", "N/A");

            String currentDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());

            //if we already won the daily challenge a snackbar is shown
            if(currentDate.equals(lastDateWon))
            {
                LinearLayout mainLayout = findViewById(R.id.main);
                Snackbar snackbar = Snackbar
                        .make(mainLayout, "Daily challenge déjà gagnée", Snackbar.LENGTH_LONG);

                View snackbarView = snackbar.getView();
                TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                snackbar.show();
            } else {
                //if the daily challenge is not already won we start the game in challenge mode
                SharedPreferences.Editor editor = prefs.edit();
                editor.remove("currentScore");
                editor.remove("savedGameIsChallenge");
                editor.putBoolean("challengeMode", true);
                editor.apply();

                Intent intent = new Intent(this, Game2048.class);
                intent.putExtra("challengeMode", true);
                startActivity(intent);
            }


        });

    }

    //we set a new challenge daily for a score between 2048 and 3074 and the time limit is 3minutes
    public static DailyChallengeActivity dailyChallenge(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("DailyChallengePrefs", Context.MODE_PRIVATE);
        String lastDate = prefs.getString("lastDate", "");
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(new Date());

        if (!today.equals(lastDate)) {
            int target = 2048 + new Random().nextInt(1026);
            long timeLimit = 3 * 60 * 1000;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("lastDate", today);
            editor.putInt("targetScore", target);
            editor.putLong("timeLimit", timeLimit);
            editor.apply();
        }

        int score = prefs.getInt("targetScore", 12);
        long limit = prefs.getLong("timeLimit", 3 * 60 * 1000);

        return new DailyChallengeActivity(score, limit);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}