package com.example.a2048_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class StatisticActivity  extends BaseActivity {

    private int bestScore = 0;
    private int playedGame = 0;
    private int gameWon = 0;
    private int gameLost = 0;
    private DataBase db;
    private TextView playedGameScore;
    private TextView top1Score;
    private TextView playedGameLosedGameScore;

    private TextView lastDailyChallengeWon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        //load all the information of the database in the textView
        db = new DataBase(this);
        playedGameScore = findViewById(R.id.tvPlayedGameScore);
        top1Score = findViewById(R.id.affichage_best_score);
        playedGameLosedGameScore = findViewById(R.id.tvPlayedGameLosedGameScore);
        lastDailyChallengeWon = findViewById(R.id.tvDateLastDailyChallengeWon);

        bestScore = db.getTheTopScore();
        top1Score.setText(String.valueOf(bestScore));

        playedGame = db.getPlayedGame();
        playedGameScore.setText(String.valueOf(playedGame));

        gameWon = db.getGameWon();
        gameLost = playedGame - gameWon;
        playedGameLosedGameScore.setText(String.valueOf(gameWon + "/" + gameLost));

        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        String lastDateWon = prefs.getString("lastDailyChallengeWon", "N/A");

        lastDailyChallengeWon.setText(String.valueOf(lastDateWon));

        Button menuButton = findViewById(R.id.btnReturnMenu);
        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(StatisticActivity.this, HighScoreActivity.class);
            startActivity(intent);
        });
    }






}
