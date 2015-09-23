package com.orangemako.minesweeper.game;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orangemako.minesweeper.R;
import com.orangemako.minesweeper.api.FinanceService;
import com.orangemako.minesweeper.api.StockQuoteResponse;
import com.orangemako.minesweeper.board.Board;
import com.orangemako.minesweeper.board.BoardLayoutView;
import com.orangemako.minesweeper.drawable.ConcentricCirclesDrawable;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class GameActivity extends AppCompatActivity implements GameManager.Listener {
    static final int IN_PLAY_LEVEL = 0;
    static final int WON_LEVEL = 1;
    static final int LOST_LEVEL = 2;

    @Bind(R.id.board_layout_view) BoardLayoutView mBoardLayoutView;
    @Bind(R.id.remaining_flags_text_view) TextView mRemainingFlagsTextView;
    @Bind(R.id.elapsed_time_text_view) TextView mElapsedTimeTextView;
    @Bind(R.id.finish_button) Button mFinishButton;
    @Bind(R.id.reset_button) Button mResetButton;
    @Bind(R.id.status_image_view) ImageView mStatusImageView;

    private GameManager mGameManager;
    private int mDimension = Board.DEFAULT_DIMENSION;
    private int mNumMines = Board.DEFAULT_NUM_MINES;
    private LevelListDrawable mStatusImageDrawable;

    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        setupViews();
        setupGame();

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://query.yahooapis.com/v1/public/yql").build();
        FinanceService financeService = restAdapter.create(FinanceService.class);
        financeService.getStockQuote(new Callback<StockQuoteResponse>() {
            @Override
            public void success(StockQuoteResponse s, Response response) {
                s.toString();

            }

            @Override
            public void failure(RetrofitError error) {
                error.toString();
            }
        });
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
                onGameFinished();
            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mGameManager.initGame(mDimension, mNumMines);

                    stopTimer();
                    startTimer();
                    mStatusImageDrawable.setLevel(IN_PLAY_LEVEL);
                }
                catch (Exception e) {
                    Context context = GameActivity.this;
                    String message = context.getResources().getString(R.string.game_reset_error);
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        setupStatusImageView();
    }

    private void setupStatusImageView() {
        float fillPercent = 0.8f;
        int inPlayOuter = getResources().getColor(R.color.blue_grey_300);
        int inPlayInner = getResources().getColor(R.color.blue_grey_600);

        mStatusImageDrawable = new LevelListDrawable();
        mStatusImageDrawable.addLevel(0, IN_PLAY_LEVEL, new ConcentricCirclesDrawable(new int[]{inPlayOuter, inPlayInner}, fillPercent));
        mStatusImageDrawable.addLevel(0, WON_LEVEL, new ConcentricCirclesDrawable(new int[]{Color.GREEN, Color.YELLOW}, fillPercent));
        mStatusImageDrawable.addLevel(0, LOST_LEVEL, new ConcentricCirclesDrawable(new int[]{Color.RED, Color.BLACK}, fillPercent));

        mStatusImageView.setBackground(mStatusImageDrawable);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mGameManager != null) {
            updateMineFlagsRemainingCount(mGameManager.getMineFlagsRemainingCount());
            startTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
    }

    void startTimer() {
        if(mGameManager != null && !mGameManager.isGameFinished()) {
            mGameManager.startTimer();

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateTimeElapsed(mGameManager.getElapsedTime());
                        }
                    });
                }
            };

            mTimer = new Timer();

            // Delay: 0, Interval: 1000ms
            mTimer.schedule(timerTask, 0, 1000);
        }
    }


    void stopTimer() {
        if(mGameManager != null && mTimer != null) {
            mGameManager.stopTimer();

            mTimer.cancel();
            mTimer = null;
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
        String message = getResources().getString(R.string.loss_message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        mStatusImageDrawable.setLevel(LOST_LEVEL);
    }

    @Override
    public void onWin() {
        String message = getResources().getString(R.string.win_message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        mStatusImageDrawable.setLevel(WON_LEVEL);
    }

    @Override
    public void onGameFinished() {
        stopTimer();
    }
}
