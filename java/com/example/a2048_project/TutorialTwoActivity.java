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
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;



public class TutorialTwoActivity extends BaseActivity {

    private GameGrid gameGrid;
    private TextView[][] tileViews4x4 = new TextView[4][4];
    private GridLayout gridLayout;

    private boolean tutorialCompleted = false;

    private boolean merge = false;


    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutorial_two);

        //initialyse the gamegrid
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

        //on tonch listener for the action of the grid
        gridLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeTop() {
                if (gameGrid.moveUp()) {
                    updateUI();
                    gameGrid.spawnTile();
                }
            }

            @Override
            public void onSwipeRight() {
                if (gameGrid.moveRight()) {
                    updateUI();
                    gameGrid.spawnTile();
                }
            }

            @Override
            public void onSwipeLeft() {
                if (gameGrid.moveLeft()) {
                    updateUI();
                    gameGrid.spawnTile();
                }
            }

            @Override
            public void onSwipeBottom() {
                if (gameGrid.moveDown()) {
                    updateUI();
                    gameGrid.spawnTile();
                }
            }
        });

        //get all the imageView in the xml
        ImageView tileMergeRight = findViewById(R.id.tileAnimationMerge2);
        ImageView tileMergeLeft = findViewById(R.id.tileAnimationMerge1);
        ImageView tileMerged = findViewById(R.id.tileAnimationMerged);
        ImageView arrow = findViewById(R.id.leftArrowAnimation);

        //start the animation
        startMergeAnimationLoop(tileMergeLeft, tileMergeRight, tileMerged, arrow);

    }

    private void startMergeAnimationLoop(ImageView tileMergeLeft, ImageView tileMergeRight, ImageView tileMerged,  ImageView arrow) {
        //put the two tiles 2 visible and the tiles 4 invisible
        tileMergeLeft.setVisibility(View.VISIBLE);
        tileMergeRight.setVisibility(View.VISIBLE);
        tileMergeRight.setTranslationX(0); // reset the position of the 2 of the right
        tileMerged.setVisibility(View.INVISIBLE);

        //move the tile 2 to the left
        ObjectAnimator moveTileLeft = ObjectAnimator.ofFloat(tileMergeRight, "translationX", 0f, -305f);
        moveTileLeft.setDuration(500);

        //move the arrow to the left
        ObjectAnimator moveArrow = ObjectAnimator.ofFloat(arrow, "translationX", 0f, -305f);
        moveTileLeft.setDuration(800);

        //pause of 200ms
        ValueAnimator pause = ValueAnimator.ofInt(0, 0);
        pause.setDuration(200);

        //put the animation in a sequence
        AnimatorSet sequence = new AnimatorSet();
        sequence.playSequentially(moveArrow,moveTileLeft,  pause);

        //start the animation and redo at the end of this one
        sequence.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //we put the two 2 at invisible
                tileMergeLeft.setVisibility(View.INVISIBLE);
                tileMergeRight.setVisibility(View.INVISIBLE);

                //the four is now visible and put to front
                tileMerged.setVisibility(View.VISIBLE);
                tileMerged.bringToFront();

                //micro delayed the end of the animation and redo the loop
                tileMerged.postDelayed(() -> {
                    tileMerged.setVisibility(View.INVISIBLE);
                    startMergeAnimationLoop(tileMergeLeft, tileMergeRight, tileMerged, arrow);
                }, 400);
            }
        });

        sequence.start();
    }



    private void updateUI() {
        Context context = this;
        Cell[][] cells = gameGrid.getCells();

        //update the gamegrid
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

        //once we do a merge the tutorial is complete we can launch game2048
        if (gameGrid.isMerged() && !tutorialCompleted) {
            tutorialCompleted = true;
            new AlertDialog.Builder(context)
                    .setTitle("Fin du tutoriel")
                    .setMessage("Bravo vous savez désormer déplacer une tuile, il ne vous reste plus qu'a jouer et atteindre la tuile 2048 ou plus.")
                    .setPositiveButton("Jouer à 2048", (dialog, which) -> {
                        Intent intent = new Intent(TutorialTwoActivity.this, Game2048.class);
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
        switch(theme) {
            case 1:
                switch(value) {
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
                switch(value) {
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
                switch(value) {
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