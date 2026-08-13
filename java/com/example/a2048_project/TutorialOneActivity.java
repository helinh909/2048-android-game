package com.example.a2048_project;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;


public class TutorialOneActivity extends BaseActivity {

    private GameGrid gameGrid;
    private TextView[][] tileViews4x4 = new TextView[4][4];
    private GridLayout gridLayout;

    private boolean tutorialCompleted = false;

    private boolean leftMovement = false;
    private boolean rightMovement = false;
    private boolean topMovement = false;
    private boolean downMovement = false;

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutorial_one);

        //initialyse and get the information needed for the gameGrid
        gridLayout = findViewById(R.id.gameGrid);
        gameGrid = new GameGrid(this);

        Context context = this;
        DataBase db = new DataBase(context);
        db.decreaseGamesPlayed();

        gridLayout.removeAllViews();
        gridLayout.setColumnCount(4);
        gridLayout.setRowCount(4);


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
        updateUI();

        //on swipe listener
        gridLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeTop() {
                if (gameGrid.moveUp()) {
                    updateUI();
                    topMovement = true;
                }
            }

            @Override
            public void onSwipeRight() {
                if (gameGrid.moveRight()) {
                    updateUI();
                    rightMovement = true;
                }
            }

            @Override
            public void onSwipeLeft() {
                if (gameGrid.moveLeft()) {
                    updateUI();
                    leftMovement = true;
                }
            }

            @Override
            public void onSwipeBottom() {
                if (gameGrid.moveDown()) {
                    updateUI();
                    downMovement = true;
                }
            }
        });


        ImageView tile = findViewById(R.id.tileAnimation);

        //Animation to move the tile to the right and go back
        ObjectAnimator moveRight = ObjectAnimator.ofFloat(tile, "translationX", 0f, 100f);
        moveRight.setDuration(500);
        moveRight.setRepeatMode(ValueAnimator.REVERSE);
        moveRight.setRepeatCount(1);

        //Animation to move the tile to the bottom and go back
        ObjectAnimator moveDown = ObjectAnimator.ofFloat(tile, "translationY", 0f, 100f);
        moveDown.setDuration(500);
        moveDown.setRepeatMode(ValueAnimator.REVERSE);
        moveDown.setRepeatCount(1);

        //Animation to move the tile to the left and go back
        ObjectAnimator moveLeft = ObjectAnimator.ofFloat(tile, "translationX", 0f, -100f);
        moveLeft.setDuration(500);
        moveLeft.setRepeatMode(ValueAnimator.REVERSE);
        moveLeft.setRepeatCount(1);

        //Animation to move the tile up and go back
        ObjectAnimator moveUp = ObjectAnimator.ofFloat(tile, "translationY", 0f, -100f);
        moveUp.setDuration(500);
        moveUp.setRepeatMode(ValueAnimator.REVERSE);
        moveUp.setRepeatCount(1);

        //put all the animation in a sequence
        AnimatorSet sequence = new AnimatorSet();
        sequence.playSequentially(moveRight, moveDown, moveLeft, moveUp);

        //Redo the sequence at the end of each one
        sequence.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                sequence.start();
            }
        });

        //start the sequence
        sequence.start();
    }




    private void updateUI() {
        //update the gamegrid
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
        Context context = this;

        //if we did all the movement, an alertDialog is shown to go to tutorial two
        if (topMovement && rightMovement && leftMovement && downMovement && !tutorialCompleted) {
            tutorialCompleted = true;
            new AlertDialog.Builder(context)
                    .setTitle("Fin du premier tutoriel")
                    .setMessage("Bravo vous savez désormer déplacer une tuile")
                    .setPositiveButton("Suite du tutoriel", (dialog, which) -> {
                        Intent intent = new Intent(TutorialOneActivity.this, TutorialTwoActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        }
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
}