package com.example.a2048_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameGrid {
    private final int size4x4 = 4;
    private Cell[][] cells;
    private Random random = new Random();

    private Context context;
    private int score;
    private boolean merged = false;
    private int nombreDeplacements;
    private int casePlusHaute;

    private boolean merge = false;
    private int tileNumber;

    private Boolean continueToPlay = false;

    public GameGrid(Context context)
    {
        cells = new Cell[size4x4][size4x4];
        for (int i = 0; i < size4x4; i++) {
            for (int j = 0; j < size4x4; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
        spawnTile();
        spawnTile();
        this.context = context;

        DataBase db = new DataBase(context);
        db.incrementGamesPlayed();
    }

    public void restart()
    {
        for (int i = 0; i < size4x4; i++) {
            for (int j = 0; j < size4x4; j++) {
                cells[i][j].clearTile();
            }
        }
        spawnTile();
        spawnTile();
    }

    public void spawnTile() {
        List<Cell> emptyCells = new ArrayList<>();
        for (int i = 0; i < size4x4; i++) {
            for (int j = 0; j < size4x4; j++) {
                if (cells[i][j].isEmpty()) {
                    emptyCells.add(cells[i][j]);
                }
            }
        }
        if (!emptyCells.isEmpty()) {
            Cell cell = emptyCells.get(random.nextInt(emptyCells.size()));
            int value = random.nextDouble() < 0.67 ? 2 : 4; // 67% that the tile that is spawn is a 2, else it's a 4
            cell.setTile(new Tile(value));
        }
    }

    public boolean moveUp()
    {
        boolean moved = false;
        for (int j = 0; j < size4x4; j++) {
            List<Tile> colTiles = new ArrayList<>();
            for (int i = 0; i < size4x4; i++) {
                if (!cells[i][j].isEmpty()) {
                    colTiles.add(cells[i][j].getTile());
                }
            }
            for (int i = 0; i < colTiles.size() - 1; i++) {
                Tile current = colTiles.get(i);
                Tile next = colTiles.get(i + 1);
                if (current.canMergeWith(next)) {
                    current.mergeCalculationNumber(next);
                    colTiles.set(i + 1, null);
                    score+= current.getTileSpawnNumber();
                    tileNumber = current.getTileSpawnNumber();
                    if(current.isMerged()){
                        merged = true;
                    }
                    victory();
                    moved = true;
                    i++;
                }
            }
            List<Tile> newCol = new ArrayList<>();
            for (Tile t : colTiles) {
                if (t != null) {
                    newCol.add(t);
                }
            }

            while (newCol.size() < size4x4) {
                newCol.add(null);
            }
            for (int i = 0; i < size4x4; i++) {
                if (cells[i][j].getTile() != newCol.get(i)) {
                    cells[i][j].setTile(newCol.get(i));
                    moved = true;
                }
            }
        }
        if(moved){
            SoundManager.playMoveSound();
            casePlusHaute = getHighestTile();
            nombreDeplacements++;
        }
        gameEnd();
        return moved;
    }

    public boolean moveDown() {
        boolean moved = false;
        for (int j = 0; j < size4x4; j++) {
            List<Tile> colTiles = new ArrayList<>();
            for (int i = size4x4 - 1; i >= 0; i--) {
                if (!cells[i][j].isEmpty()) {
                    colTiles.add(cells[i][j].getTile());
                }
            }
            for (int i = 0; i < colTiles.size() - 1; i++) {
                Tile current = colTiles.get(i);
                Tile next = colTiles.get(i + 1);
                if (current.canMergeWith(next)) {
                    current.mergeCalculationNumber(next);
                    colTiles.set(i + 1, null);
                    score+= current.getTileSpawnNumber();
                    tileNumber = current.getTileSpawnNumber();
                    if(current.isMerged()){
                        merged = true;
                    }
                    victory();
                    moved = true;
                    i++;
                }
            }
            List<Tile> newCol = new ArrayList<>();
            for (Tile t : colTiles) {
                if (t != null) {
                    newCol.add(t);
                }
            }
            while (newCol.size() < size4x4) {
                newCol.add(null);
            }
            for (int i = 0; i < size4x4; i++) {
                if (cells[size4x4 - 1 - i][j].getTile() != newCol.get(i)) {
                    cells[size4x4 - 1 - i][j].setTile(newCol.get(i));
                    moved = true;
                }
            }
        }
        if(moved){
            SoundManager.playMoveSound();
            casePlusHaute = getHighestTile();
            nombreDeplacements++;
        }
        gameEnd();
        return moved;
    }

    public boolean moveRight() {
        boolean moved = false;
        for (int i = 0; i < size4x4; i++) {
            List<Tile> rowTiles = new ArrayList<>();
            for (int j = size4x4 - 1; j >= 0; j--) {
                if (!cells[i][j].isEmpty()) {
                    rowTiles.add(cells[i][j].getTile());
                }
            }
            for (int j = 0; j < rowTiles.size() - 1; j++) {
                Tile current = rowTiles.get(j);
                Tile next = rowTiles.get(j + 1);
                if (current.canMergeWith(next)) {
                    current.mergeCalculationNumber(next);
                    rowTiles.set(j + 1, null);
                    score+= current.getTileSpawnNumber();
                    tileNumber = current.getTileSpawnNumber();
                    //gameEnd();
                    if(current.isMerged()){
                        merged = true;
                    }
                    victory();
                    moved = true;
                    j++;
                }
            }
            List<Tile> newRow = new ArrayList<>();
            for (Tile t : rowTiles) {
                if (t != null) {
                    newRow.add(t);
                }
            }
            while (newRow.size() < size4x4) {
                newRow.add(null);
            }
            for (int j = 0; j < size4x4; j++) {
                if (cells[i][size4x4 - 1 - j].getTile() != newRow.get(j)) {
                    cells[i][size4x4 - 1 - j].setTile(newRow.get(j));
                    moved = true;
                }
            }
            if(moved){
                SoundManager.playMoveSound();
                nombreDeplacements++;
                casePlusHaute = getHighestTile();
            }
        }
        gameEnd();
        return moved;
    }

    public boolean moveLeft() {
        boolean moved = false;
        for (int i = 0; i < size4x4; i++) {
            List<Tile> rowTiles = new ArrayList<>();
            for (int j = 0; j < size4x4; j++) {
                if (!cells[i][j].isEmpty()) {
                    rowTiles.add(cells[i][j].getTile());
                }
            }
            for (int j = 0; j < rowTiles.size() - 1; j++) {
                Tile current = rowTiles.get(j);
                Tile next = rowTiles.get(j + 1);
                if (current.canMergeWith(next)) {
                    current.mergeCalculationNumber(next);
                    rowTiles.set(j + 1, null);
                    score+= current.getTileSpawnNumber();
                    tileNumber = current.getTileSpawnNumber();
                    if(current.isMerged()){
                        merged = true;
                    }
                    victory();
                    moved = true;
                    j++;
                }
            }
            List<Tile> newRow = new ArrayList<>();
            for (Tile t : rowTiles) {
                if (t != null) {
                    newRow.add(t);
                }
            }
            while (newRow.size() < size4x4) {
                newRow.add(null);
            }
            for (int j = 0; j < size4x4; j++) {
                if (cells[i][j].getTile() != newRow.get(j)) {
                    cells[i][j].setTile(newRow.get(j));
                    moved = true;
                }
            }
            if(moved){
                SoundManager.playMoveSound();
                casePlusHaute = getHighestTile();
                nombreDeplacements++;
            }
        }
        gameEnd();
        return moved;
    }

    public void victory() {
        if (continueToPlay)
            return;

        if (tileNumber == 2048) {
            showVictoryScreen();
            if(!continueToPlay)
            {
                DataBase db = new DataBase(context);
                db.addScore(score);
            }

        }
    }


    private void showVictoryScreen() {
        DataBase db = new DataBase(context);
        db.incrementGamesWon();
        new AlertDialog.Builder(context)
                .setTitle("Victoire")
                .setMessage("Bravo vous avez atteint le score 2048")
                .setPositiveButton("Continuer de jouer", (dialog, which) -> continueToPlay = true)
                .setNegativeButton("Revenir au menu", (dialog, which) -> {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                })
                //Generate the image and share the image
                .setNeutralButton("Partager mon score", (dialog, which) -> {
                    File recapImage = generateScoreImage();
                    if (recapImage != null) {
                        shareScoreImage(recapImage);
                    }
                })
                .setCancelable(false)
                .show();
    }


    void gameEnd() {

        //Check if there are empty cells
        for (int i = 0; i < size4x4; i++) {
            for (int j = 0; j < size4x4; j++) {
                if (cells[i][j].isEmpty()) {
                    return;//There is empty cells so return
                }
            }
        }

        //Verify if fusion is possible
        for (int i = 0; i < size4x4; i++) {
            for (int j = 0; j < size4x4; j++) {
                Tile current = cells[i][j].getTile();

                //Verify if the currents cells is empty
                if (current == null) continue;
                int value = current.getTileSpawnNumber();

                //Check throught the right
                if (j < size4x4 - 1 && cells[i][j+1].getTile() != null) {
                    if (value == cells[i][j+1].getTile().getTileSpawnNumber()) {
                        return;
                    }
                }

                //Check throught the bottom
                if (i < size4x4 - 1 && cells[i+1][j].getTile() != null) {
                    if (value == cells[i+1][j].getTile().getTileSpawnNumber()) {
                        return;
                    }
                }

                // Check throught the left
                if (j > 0 && cells[i][j-1].getTile() != null) {
                    if (value == cells[i][j-1].getTile().getTileSpawnNumber()) {
                        return;
                    }
                }

                //Check throught the above
                if (i > 0 && cells[i-1][j].getTile() != null) {
                    if (value == cells[i-1][j].getTile().getTileSpawnNumber()) {
                        return;
                    }
                }
            }
        }

        //No fusion available and no empty cells so we show the looser screen
        showLooserScreen();
    }

    private void showLooserScreen() {
        DataBase db = new DataBase(context);
        db.addScore(score);
        new AlertDialog.Builder(context)
                .setTitle("Défaite")
                .setMessage("Vous ne pouvez plus faire de déplacements, vous avez perdu avec "+ score + ".")
                .setPositiveButton("Recommencer", (dialog, which) -> restart())
                .setNegativeButton("Revenir au menu", (dialog, which) -> {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                })
                //share the score like the victory screen
                .setNeutralButton("Partager mon score", (dialog, which) -> {
                    File imageFile = generateScoreImage();
                    if (imageFile != null && imageFile.exists()) {
                        Log.d("DEBUG", "Image créée : " + imageFile.getAbsolutePath());
                        shareScoreImage(imageFile);
                    } else {
                        Log.e("ERROR", "Échec de la création de l'image");
                    }
                })
                .setCancelable(false)
                .show();
    }

    private File generateScoreImage() {
        try {
            //Put the layout activityRecapitulative in a view
            LayoutInflater inflater = LayoutInflater.from(context);
            View recapView = inflater.inflate(R.layout.activity_recapitulative, null);

            //Change the values of the different textview
            ((TextView) recapView.findViewById(R.id.tvMoveCountScore)).setText(String.valueOf(nombreDeplacements));
            ((TextView) recapView.findViewById(R.id.tvHighestTileScore)).setText(String.valueOf(casePlusHaute));
            ((TextView) recapView.findViewById(R.id.tvScoreNumber)).setText(String.valueOf(score));

            //Convert the view to bitmap
            recapView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            recapView.layout(0, 0, recapView.getMeasuredWidth(), recapView.getMeasuredHeight());

            Bitmap bitmap = Bitmap.createBitmap(recapView.getWidth(), recapView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            recapView.draw(canvas);

            //Save the image in a temporary file
            File picturesDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "");
            if (!picturesDir.exists()) {
                picturesDir.mkdirs();  //make the directory if it doesn't exist
            }
            File imageFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "score_recap.png");
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            return imageFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    //colect the file to share and share it with the intent
    private void shareScoreImage(File imageFile) {

        Uri imageUri = FileProvider.getUriForFile(context, "com.example.a2048_project.fileprovider", imageFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        //open the android sharing menu
        context.startActivity(Intent.createChooser(shareIntent, "Partager mon score via"));
    }

    public int getHighestTile() {
        int maxTile = 0;
        for (int i = 0; i < size4x4; i++) {
            for (int j = 0; j < size4x4; j++) {
                if (!cells[i][j].isEmpty() && cells[i][j].getTile().getTileSpawnNumber() > maxTile) {
                    maxTile = cells[i][j].getTile().getTileSpawnNumber();
                }
            }
        }
        casePlusHaute = maxTile;
        return maxTile;
    }

    public Cell[][] getCells() {
        return cells;
    }


    public boolean isMerged(){
        return merged;
    }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
