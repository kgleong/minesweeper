package com.orangemako.minesweeper.board;

public class BoardSquare {
    private boolean mContainsMine = false;
    private int mAdjacentMinesCount = 0;

    private int mXGridCoordinate;
    private int mYGridCoordinate;

    public BoardSquare(int xGridCoordinate, int yGridCoordinate){
        mXGridCoordinate = xGridCoordinate;
        mYGridCoordinate = yGridCoordinate;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if(object instanceof BoardSquare) {
            BoardSquare otherSquare = (BoardSquare) object;

            if( this.mXGridCoordinate == otherSquare.mXGridCoordinate &&
                    this.mYGridCoordinate == otherSquare.mYGridCoordinate) {

                result = true;
            }
        }

        return result;
    }

    @Override
    public int hashCode() {
        StringBuilder builder = new StringBuilder();
        builder.append(mXGridCoordinate).append(',').append('y');

        return builder.toString().hashCode();
    }

    public boolean doesContainMine() {
        return mContainsMine;
    }

    public void setContainsMine(boolean containsMine) {
        mContainsMine = containsMine;
    }

    public int getAdjacentMinesCount() {
        return mAdjacentMinesCount;
    }

    public void setAdjacentMinesCount(int adjacentMinesCount) {
        mAdjacentMinesCount = adjacentMinesCount;
    }

    public int getXGridCoordinate() {
        return mXGridCoordinate;
    }

    public int getYGridCoordinate() {
        return mYGridCoordinate;
    }
}
