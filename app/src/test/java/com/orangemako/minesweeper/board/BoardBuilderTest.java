package com.orangemako.minesweeper.board;

import com.orangemako.minesweeper.exceptions.InvalidArgumentException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BoardBuilderTest {
    Board.Builder builder;
    Board board;

    int expectedDimension;
    int expectedNumMines;

    @Before
    public void setUp() throws Exception {
        builder = new Board.Builder();
    }

    @Test(expected = InvalidArgumentException.class)
    public void testDimensionWithInvalidArgs() throws Exception {
        builder.dimension(0);
    }

    @Test(expected = InvalidArgumentException.class)
    public void testNumMinesWithInvalidArgs() throws Exception {
        builder.numMines(0);
    }

    @Test
    public void testBoardDimensions() throws Exception {
        setupBoard();

        Assert.assertEquals(expectedDimension, board.getBoardGrid().length);
    }

    @Test
    public void testBoardMines() throws Exception {
        setupBoard();
        List<BoardSquare> collectedMines = new ArrayList<>();
        BoardSquare[][] grid = board.getBoardGrid();

        // Collect mines placed on grid.
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid.length; j++) {
                BoardSquare square = grid[i][j];

                if(square != null) {
                    if (square.doesContainMine()) {
                        collectedMines.add(square);
                    }
                }
            }
        }

        Assert.assertEquals(expectedNumMines, board.getMineSquares().size());
        Assert.assertEquals(expectedNumMines, collectedMines.size());
    }

    @Test
    public void testEmptySquares() throws Exception {
        setupBoard();
        BoardSquare[][] grid = board.getBoardGrid();

        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid.length; j++) {
                BoardSquare square = grid[i][j];

                // Check all empty squares to ensure no adjacent mines exist.
                if(square == null) {
                    int startingY = Math.max(0, i - 1);
                    int startingX = Math.max(0, j - 1);

                    int actualAdjacentMines = 0;

                    for(int k = startingY; k <= i + 1 && k < expectedDimension; k++) {
                        for(int m = startingX; m <= j + 1 && m < expectedDimension; m++) {
                            BoardSquare adjacentSquare = grid[k][m];

                            if(adjacentSquare != null && adjacentSquare.doesContainMine()) {
                                actualAdjacentMines++;
                            }
                        }
                    }
                    Assert.assertEquals(0, actualAdjacentMines);
                }
            }
        }
    }

    @Test
    public void testCalculatedSquares() throws Exception {
        setupBoard();
        BoardSquare[][] grid = board.getBoardGrid();

        // Check all calculated squares for correctness
        for(int i = 0; i < grid.length; i++) {
            for(int j = 0; j < grid.length; j++) {
                BoardSquare square = grid[i][j];

                if(square != null && !square.doesContainMine()) {
                    int actualAdjacentMines = 0;

                    int x = square.x;
                    int y = square.y;

                    int startingY = Math.max(0, y - 1);
                    int startingX = Math.max(0, x - 1);

                    for(int k = startingY; k <= y + 1 && k < expectedDimension; k++) {
                        for(int m = startingX; m <= x + 1 && m < expectedDimension; m++) {
                            BoardSquare adjacentSquare = grid[k][m];

                            if(adjacentSquare != null && adjacentSquare.doesContainMine()) {
                                actualAdjacentMines++;
                            }
                        }
                    }
                    Assert.assertEquals(square.adjacentMinesCount, actualAdjacentMines);
                }
            }
        }
    }

    void setupBoard() throws Exception {
        expectedDimension = 4;
        expectedNumMines = 5;

        board = builder.dimension(expectedDimension).numMines(expectedNumMines).build();
    }
}