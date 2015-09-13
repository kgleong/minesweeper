package com.orangemako.minesweeper;

import android.app.Application;

import com.squareup.otto.Bus;

public class MinesweeperApplication extends Application {
    private static Bus gameStateBus;

    @Override
    public void onCreate() {
    }

    public static Bus getGameStateBus() {
        if(gameStateBus == null) {
            gameStateBus = new Bus();
        }

        return gameStateBus;
    }
}
