package com.orangemako.minesweeper.game;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.orangemako.minesweeper.R;
import com.orangemako.minesweeper.board.Board;
import com.orangemako.minesweeper.board.BoardLayoutView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class GameActivity extends AppCompatActivity implements GameManager.Listener {
    @Bind(R.id.board_layout_view) BoardLayoutView mBoardLayoutView;
    @Bind(R.id.remaining_flags_text_view) TextView mRemainingFlagsTextView;
    @Bind(R.id.elapsed_time_text_view) TextView mElapsedTimeTextView;
    @Bind(R.id.finish_button) Button mFinishButton;
    @Bind(R.id.reset_button) Button mResetButton;

    private GameManager mGameManager;
    private int mDimension = Board.DEFAULT_DIMENSION;
    private int mNumMines = Board.DEFAULT_NUM_MINES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        setupViews();
        setupGame();
    }

    private void setupGame() {
        try {
            mGameManager = new GameManager(mDimension, mNumMines, mBoardLayoutView, this);
        }
        catch (Exception e) {
            String errorMessage = getResources().getString(R.string.board_initialization_error);
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViews() {
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameManager.finishGame();
            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mGameManager.initGame(mDimension, mNumMines);
                }
                catch(Exception e) {
                    Context context = GameActivity.this;
                    String message = context.getResources().getString(R.string.game_reset_error);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mGameManager != null) {
            mGameManager.startTimer();
            updateTimeElapsed(mGameManager.getElapsedTime());
            updateMineFlagsRemainingCount(mGameManager.getMineFlagsRemainingCount());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mGameManager != null) {
            mGameManager.stopTimer();
        }
    }

    @Override
    public void updateTimeElapsed(long elapsedTime) {
        int elapsedTimeInSeconds = (int) elapsedTime / 1000;

        mElapsedTimeTextView.setText(String.valueOf(elapsedTimeInSeconds));
    }

    @Override
    public void updateMineFlagsRemainingCount(int flagsRemaining) {
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
}
