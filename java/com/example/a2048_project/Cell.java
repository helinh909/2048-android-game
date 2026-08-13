package com.example.a2048_project;

//cell of the gamegrid
public class Cell
{
    private int row;
    private int column;
    private Tile tile;

    public Cell(int row, int column)
    {
        this.row = row;
        this.column = column;
        this.tile = null;
    }

    public boolean isEmpty()
    {
        return tile == null;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile)
    {
        this.tile = tile;
    }

    public void clearTile()
    {
        this.tile = null;
    }
}
