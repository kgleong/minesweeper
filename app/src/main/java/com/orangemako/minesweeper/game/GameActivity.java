package com.orangemako.minesweeper.game;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.orangemako.minesweeper.R;
import com.orangemako.minesweeper.board.Board;
import com.orangemako.minesweeper.board.BoardLayoutView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class GameActivity extends AppCompatActivity implements Game.GameListener{
    @Bind(R.id.board_layout_view) BoardLayoutView mBoardLayoutView;
    @Bind(R.id.remaining_flags_text_view) TextView mRemainingFlagsTextView;
    @Bind(R.id.elapsed_time_text_view) TextView mElapsedTimeTextView;

    private Game mGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        setupGame();
    }

    private void setupGame() {
        try {
            mGame = new Game(Board.DEFAULT_DIMENSION, Board.DEFAULT_NUM_MINES, this);

            mBoardLayoutView = (BoardLayoutView) findViewById(R.id.board_layout_view);
            mBoardLayoutView.setupBoard(mGame);
        }
        catch (Exception e) {
            String errorMessage = getResources().getString(R.string.board_initialization_error);
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateFlagsRemainingCount(int flagsRemaining) {
        mRemainingFlagsTextView.setText(String.valueOf(flagsRemaining));
    }

    @Override
    public void onLoss() {
        Toast.makeText(this, "LOSS", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWin() {
        Toast.makeText(this, "WON", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateTimeElapsed(int totalTime) {
        mElapsedTimeTextView.setText(String.valueOf(totalTime));
    }
}
