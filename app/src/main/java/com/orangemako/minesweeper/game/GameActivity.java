package com.orangemako.minesweeper.game;

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


public class GameActivity extends AppCompatActivity implements GameManager.GameManagerListener {
    @Bind(R.id.board_layout_view) BoardLayoutView mBoardLayoutView;
    @Bind(R.id.remaining_flags_text_view) TextView mRemainingFlagsTextView;
    @Bind(R.id.elapsed_time_text_view) TextView mElapsedTimeTextView;
    @Bind(R.id.finish_button) Button mFinishButton;
    @Bind(R.id.reset_button) Button mResetButton;

    private GameManager mGameManager;
    private int mElapsedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        setupGame();
        setupViews();
    }

    private void setupGame() {
        try {
            mGameManager = new GameManager(Board.DEFAULT_DIMENSION, Board.DEFAULT_NUM_MINES, this);
            mBoardLayoutView.setupBoard(mGameManager);
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
                // TODO: Refactor to use a Game object instead.
                boolean result = mBoardLayoutView.calculateResult();
                String message;
                if(result) {
                    message = "YOU WIN!";
                }
                else {
                    message = "YOU'RE DEAD";
                }
                GameActivity.this.mGameManager.setIsGameEnded(true);
                Toast.makeText(GameActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoardLayoutView.removeAllViews();
                setupGame();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameManager.startTimer();
        mElapsedTime = mGameManager.getElapsedTime();
        updateTimeElapsed(mElapsedTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameManager.stopTimer();
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
    public void updateTimeElapsed(int elapsedTime) {
        mElapsedTimeTextView.setText(String.valueOf(elapsedTime));
    }
}
