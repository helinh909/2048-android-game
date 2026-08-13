package com.example.a2048_project;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Game2048 extends BaseActivity {
    int bestScore = 0;
    int currentScore;
    private GameGrid gameGrid;
    private TextView[][] tileViews4x4 = new TextView[4][4];
    private GridLayout gridLayout;

    private TextView score;

    private TextView top1Score;

    private TextView countdown;
    private TextView countdownText;
    private TextView goalScore;
    private TextView goalScoreText;

    private MediaPlayer gameMusic;
    private CountDownTimer timer;
    private DailyChallengeActivity challenge;
    private boolean challengeMode = false;

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game2048);

        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        boolean resumeGame = getIntent().getBooleanExtra("resumeGame", false);
        challengeMode = getIntent().getBooleanExtra("challengeMode", false);

        if (resumeGame) {
            challengeMode = prefs.getBoolean("savedGameIsChallenge", false);
        }

        prefs.edit().putBoolean("challengeMode", challengeMode).apply();

        countdown = findViewById(R.id.tvTimerCountdown);
        countdownText = findViewById(R.id.timer);
        goalScore = findViewById(R.id.goalScore);
        goalScoreText = findViewById(R.id.tvGoalScore);

        //hide all the view element of the challenge mode if we are not in it
        if (!challengeMode) {
            countdown.setVisibility(View.INVISIBLE);
            countdownText.setVisibility(View.INVISIBLE);
            goalScore.setVisibility(View.INVISIBLE);
            goalScoreText.setVisibility(View.INVISIBLE);
        }

        //start the challenge mode if we are in it
        if (challengeMode) {
            startChallenge();
        }
        //stop the menu music and initialyse the soundManager for the FX
        MusicManager.stopMusic();
        SoundManager.init(this);

        gridLayout = findViewById(R.id.gameGrid);
        gameGrid = new GameGrid(this);


        // Configuration du GridLayout (4 colonnes, 4 lignes)
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(4);
        gridLayout.setRowCount(4);

        //initialise the gameGrids
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                TextView tv = new TextView(this);
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(24);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i, 1f);
                params.columnSpec = GridLayout.spec(j, 1f);
                params.width = 0;
                params.height = 0;
                params.setMargins(5, 5, 5, 5);
                tv.setLayoutParams(params);
                tv.setBackgroundResource(R.drawable.border);
                gridLayout.addView(tv);
                tileViews4x4[i][j] = tv;

            }
        }
        score = findViewById(R.id.affichage_score);
        top1Score = findViewById(R.id.affichage_best_score);

        //load the game if the extra resumeGame = true
        if (getIntent().getBooleanExtra("resumeGame", false)) {
            loadGameState();
        }

        //start the gameMusic
        MusicManager.startGameMusic(this);

        updateUI();

        //on touch listener for the swipe action on the grid
        //on every swipe we spawn a new tile, we update the UI and we check the score if we are in challenge mode
        gridLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeTop() {
                if (gameGrid.moveUp()) {
                    gameGrid.spawnTile();
                    if(challengeMode) {
                        checkScore();
                    }
                    updateUI();
                }
            }

            @Override
            public void onSwipeRight() {
                if (gameGrid.moveRight()) {
                    gameGrid.spawnTile();
                    if(challengeMode) {
                        checkScore();
                    }
                    updateUI();
                }
            }

            @Override
            public void onSwipeLeft() {
                if (gameGrid.moveLeft()) {
                    gameGrid.spawnTile();
                    if(challengeMode) {
                        checkScore();
                    }
                    updateUI();
                }
            }

            @Override
            public void onSwipeBottom() {
                if (gameGrid.moveDown()) {
                    gameGrid.spawnTile();
                    if(challengeMode) {
                        checkScore();
                    }
                    updateUI();
                }
            }
        });

        //button to redo the turorial
        Button tutorialButton = findViewById(R.id.btnTutorial);
        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Game2048.this, TutorialOneActivity.class);
                startActivity(intent);
            }
        });

        //button to go to the menu
        Button menuButton = findViewById(R.id.btnMenu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Game2048.this, MainActivity.class);
                gameGrid.setScore(currentScore);
                startActivity(intent);
            }
        });

        //button to restart
        Button restartButton = findViewById(R.id.btnRestart);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameGrid.restart();
                currentScore = 0;
                gameGrid.setScore(currentScore); //the score is set to 0
                if(challengeMode) {
                    //timer is canceled
                    if (timer != null) {
                        timer.cancel();
                    }

                    //and here the timer is restarting
                    timer = new CountDownTimer(challenge.timeLimitMillis, 1000) {
                        public void onTick(long millisUntilFinished) {
                            countdown.setText(String.valueOf(millisUntilFinished / 1000));
                        }

                        //the challenge is finished and we didn't won by the end of the timer
                        public void onFinish() {
                            if (gameGrid.getScore() < challenge.targetScore) {
                                endChallenge(false);
                            }
                        }
                    }.start();
                }
                updateUI();
            }
        });

        //to not show the tutorialActivity on the backpressed button of the phone
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(Game2048.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this,onBackPressedCallback);
    }
    private void updateUI() {

        DataBase db = new DataBase(this);

        //every time we update the text and the background of the tiles of the gameGrid
        Cell[][] cells = gameGrid.getCells();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Tile tile = cells[i][j].getTile();
                if (tile != null && tile.getTileSpawnNumber() != 0) {
                    tileViews4x4[i][j].setText(String.valueOf(tile.getTileSpawnNumber()));
                    tileViews4x4[i][j].setBackgroundColor(setTileColor(tile.getTileSpawnNumber()));
                } else {
                    tileViews4x4[i][j].setText("");
                    tileViews4x4[i][j].setBackgroundColor(setTileColor(0));
                }
            }
        }
        currentScore = gameGrid.getScore();

        //SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);7
        //update the score
        score.setText(String.valueOf(currentScore));

        bestScore = db.getTheTopScore();
        if(bestScore != 0)
        {
            top1Score.setText(String.valueOf(bestScore));
        } else {
            top1Score.setText(String.valueOf(""));
        }
        saveGameState();
    }



    //start of the daily challenge with the timer and the score
    private void startChallenge() {
        challenge = MainActivity.dailyChallenge(this);
        challengeMode = true;
        timer = new CountDownTimer(challenge.timeLimitMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                countdown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                if (gameGrid.getScore() < challenge.targetScore) {
                    endChallenge(false);
                }
            }
        }.start();
        goalScoreText.setText(String.valueOf(challenge.targetScore));

    }

    //allow to know if we exceed the score
    private void checkScore() {
        if (challengeMode && gameGrid.getScore() >= challenge.targetScore) {
            endChallenge(true);
        }
    }
    Context context = this;

    //if the challenge is won or not, alertDialog and also save the date if we won
    private void endChallenge(boolean won) {
        if (timer != null)
            timer.cancel();
        if (!challengeMode) {
            return;
        }
        String message = won ? "Bravo, vous maniez cette grille comme personne !" : "Dommage, vous avez échoué aujourd'hui, retentez demain !";

        new AlertDialog.Builder(this)
                .setTitle("Défi quotidien")
                .setMessage(message)
                .setPositiveButton("Revenir au menu", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    if (won) { //the current date is saved in the dataBase in french format if won
                        DataBase db = new DataBase(context);
                        db.incrementGamesWon();
                        String currentDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
                        editor.putString("lastDailyChallengeWon", currentDate);
                    }
                    editor.remove("currentScore");
                    editor.remove("savedGameIsChallenge");
                    editor.putBoolean("challengeMode", false);
                    editor.apply();
                    finish();
                })
                .setCancelable(false)
                .show();

        challengeMode = false;
    }





    //color for the tiles
    private int setTileColor(int value) {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        int theme = prefs.getInt("app_theme", 0);
        switch (theme) {
            case 1:
                switch (value) {
                    case 2:
                        return getResources().getColor(R.color.tile2_dark, null);
                    case 4:
                        return getResources().getColor(R.color.tile4_dark, null);
                    case 8:
                        return getResources().getColor(R.color.tile8_dark, null);
                    case 16:
                        return getResources().getColor(R.color.tile16_dark, null);
                    case 32:
                        return getResources().getColor(R.color.tile32_dark, null);
                    case 64:
                        return getResources().getColor(R.color.tile64_dark, null);
                    case 128:
                        return getResources().getColor(R.color.tile128_dark, null);
                    case 256:
                        return getResources().getColor(R.color.tile256_dark, null);
                    case 512:
                        return getResources().getColor(R.color.tile512_dark, null);
                    case 1024:
                        return getResources().getColor(R.color.tile1024_dark, null);
                    case 2048:
                        return getResources().getColor(R.color.tile2048_dark, null);
                    default:
                        return getResources().getColor(R.color.grey, null);
                }

            case 2:
                switch (value) {
                    case 2:
                        return getResources().getColor(R.color.tile2_colorful, null);
                    case 4:
                        return getResources().getColor(R.color.tile4_colorful, null);
                    case 8:
                        return getResources().getColor(R.color.tile8_colorful, null);
                    case 16:
                        return getResources().getColor(R.color.tile16_colorful, null);
                    case 32:
                        return getResources().getColor(R.color.tile32_colorful, null);
                    case 64:
                        return getResources().getColor(R.color.tile64_colorful, null);
                    case 128:
                        return getResources().getColor(R.color.tile128_colorful, null);
                    case 256:
                        return getResources().getColor(R.color.tile256_colorful, null);
                    case 512:
                        return getResources().getColor(R.color.tile512_colorful, null);
                    case 1024:
                        return getResources().getColor(R.color.tile1024_colorful, null);
                    case 2048:
                        return getResources().getColor(R.color.tile2048_colorful, null);
                    default:
                        return getResources().getColor(R.color.white, null);
                }
            default:
                switch (value) {
                    case 2:
                        return getResources().getColor(R.color.tile2_light, null);
                    case 4:
                        return getResources().getColor(R.color.tile4_light, null);
                    case 8:
                        return getResources().getColor(R.color.tile8_light, null);
                    case 16:
                        return getResources().getColor(R.color.tile16_light, null);
                    case 32:
                        return getResources().getColor(R.color.tile32_light, null);
                    case 64:
                        return getResources().getColor(R.color.tile64_light, null);
                    case 128:
                        return getResources().getColor(R.color.tile128_light, null);
                    case 256:
                        return getResources().getColor(R.color.tile256_light, null);
                    case 512:
                        return getResources().getColor(R.color.tile512_light, null);
                    case 1024:
                        return getResources().getColor(R.color.tile1024_light, null);
                    case 2048:
                        return getResources().getColor(R.color.tile2048_light, null);
                    default:
                        return getResources().getColor(R.color.white, null);
                }
        }
    }

    //we save the state of our game, the score, the time remaining
    private void saveGameState() {
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        //save if the game mode is challenge mode
        editor.putBoolean("challengeMode", challengeMode);
        editor.putBoolean("savedGameIsChallenge", challengeMode);

        //save the time of the time in challenge mode
        if (challengeMode && timer != null) {
            long remainingTime = challenge.timeLimitMillis - (System.currentTimeMillis() - challenge.startTimeMillis);
            editor.putLong("remainingTime", remainingTime);
        }

        //save the state of the grid
        Cell[][] cells = gameGrid.getCells();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Tile tile = cells[i][j].getTile();
                int value = (tile != null) ? tile.getTileSpawnNumber() : 0;
                editor.putInt("cell_" + i + "_" + j, value);
            }
        }

        editor.putInt("currentScore", currentScore);

        editor.apply();
    }


    @Override
    protected void onPause() {
        super.onPause();

        MusicManager.stopMusic();

        //save the score of the game
        SharedPreferences prefs = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("score", gameGrid.getScore());
        editor.apply();

        //saving the game State
        saveGameState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        MusicManager.startGameMusic(this);

        //retrieve and show the score
        SharedPreferences prefs = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        currentScore = prefs.getInt("score", 0);
        score.setText(String.valueOf(currentScore));

    }

    //release the music if the activity is destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();

        MusicManager.stopMusic();
        SoundManager.release();
    }


    //load the game if the gamed was paused
    private void loadGameState() {
        SharedPreferences prefs = getSharedPreferences("GameData", MODE_PRIVATE);
        boolean savedIsChallenge = prefs.getBoolean("savedGameIsChallenge", false);
        String lastDailyChallengeWon = prefs.getString("lastDailyChallengeWon", "N/A");
        String currentDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());

        //a daily is already won we clear the editor
        if (savedIsChallenge && currentDate.equals(lastDailyChallengeWon)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            return;
        }

        //load the values of the tiles gameGrid
        Cell[][] cells = gameGrid.getCells();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int value = prefs.getInt("cell_" + i + "_" + j, 0);
                if (value > 0) {
                    cells[i][j].setTile(new Tile(value));
                } else {
                    cells[i][j].clearTile();
                }
            }
        }

        currentScore = prefs.getInt("currentScore", 0);
        score.setText(String.valueOf(currentScore));

        //load the time of the challengeMode if we are in it and if there is time left
        if (challengeMode) {
            long remainingTime = prefs.getLong("remainingTime", challenge.timeLimitMillis);
            if (remainingTime > 0) {
                startChallengeWithTime(remainingTime);
            } else {
                startChallenge();
            }
        }

        gameGrid.setScore(currentScore);
        updateUI();
    }

    //start the daily challenge with the time set in Main Activity (3minutes)
    private void startChallengeWithTime(long remainingTime) {
        challenge = MainActivity.dailyChallenge(this);
        challengeMode = true;
        timer = new CountDownTimer(remainingTime, 1000) {
            public void onTick(long millisUntilFinished) {
                countdown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                if (gameGrid.getScore() < challenge.targetScore) {
                    endChallenge(false);
                }
            }
        }.start();
        goalScoreText.setText(String.valueOf(challenge.targetScore));
    }


}