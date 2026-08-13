package com.example.a2048_project;

//tile of the gameGrid

public class Tile
{
    private int tileSpawnNumber;
    private boolean merged = false;
    public Tile(int tileSpawnNumber) {
        this.tileSpawnNumber = tileSpawnNumber;
    }

    public int getTileSpawnNumber() {
        return tileSpawnNumber;
    }

    public void setTileSpawnNumber(int tileSpawnNumber) {
        this.tileSpawnNumber = tileSpawnNumber;
    }

    public boolean canMergeWith(Tile anotherTile)
    {
        if(anotherTile == null){
            return false;
        } else {
            return this.tileSpawnNumber == anotherTile.tileSpawnNumber;
        }
    }

    public void mergeCalculationNumber(Tile anotherTile)
    {
        if(canMergeWith(anotherTile)){
            SoundManager.playMergeSound();
            this.tileSpawnNumber = getTileSpawnNumber()*2;
            anotherTile.setTileSpawnNumber(0);
            merged = true;
        }
    }

    public boolean isMerged() {
        return merged;
    }
}
