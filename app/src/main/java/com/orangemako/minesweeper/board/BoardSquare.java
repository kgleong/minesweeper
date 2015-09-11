package com.orangemako.minesweeper.board;

public class BoardSquare {
    boolean containsMine = false;
    int adjacentMinesCount = 0;

    int x;
    int y;

    public BoardSquare(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;

        if(object instanceof BoardSquare) {
            BoardSquare otherSquare = (BoardSquare) object;

            if(this.x == otherSquare.x && this.y == otherSquare.y) {
                result = true;
            }
        }

        return result;
    }

    @Override
    public int hashCode() {
        StringBuilder builder = new StringBuilder();
        builder.append(x).append(',').append('y');

        return builder.toString().hashCode();
    }

    public boolean doesContainMine() {
        return containsMine;
    }
}
