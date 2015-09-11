package com.orangemako.minesweeper.game;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.orangemako.minesweeper.R;
import com.orangemako.minesweeper.board.Board;
import com.orangemako.minesweeper.board.BoardLayoutView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class GameActivity extends AppCompatActivity {
    @Bind(R.id.board_layout_view)
    BoardLayoutView mBoardLayoutView;

    private Board mBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ButterKnife.bind(this);

        setupBoard();
    }

    private void setupBoard() {
        try {
            mBoard = (new Board.Builder()).dimension(8).build();
            mBoardLayoutView.setBoard(mBoard);
        } catch (Exception e) {
            String errorMessage = getResources().getString(R.string.board_initialization_error);
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
