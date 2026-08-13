package com.example.a2048_project;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import java.util.List;

public class HighScoreActivity extends BaseActivity {

    private DataBase db;
    private ListView scoreListView;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        db = new DataBase(this);
        scoreListView = findViewById(R.id.scoreListView);
        resetButton = findViewById(R.id.resetButton);

        //load the top 10 best score
        loadScores();

        //reset all the scores
        resetButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Réinitialisation")
                    .setMessage("Voulez-vous vraiment réinitialiser les scores ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        db.resetScores();
                        loadScores();
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

        //button to go to the statistic
        Button statisticButton = findViewById(R.id.btnStat);
        statisticButton.setOnClickListener(v -> {
            Intent intent = new Intent(HighScoreActivity.this, StatisticActivity.class);
            startActivity(intent);
        });

        //button to go to the menu
        Button menuButton = findViewById(R.id.btnReturnMenu);
        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(HighScoreActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }

    //get the top 10 score
    private void loadScores() {
        List<Integer> scores = db.getTopScores();
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scores);
        scoreListView.setAdapter(adapter);
    }
}
