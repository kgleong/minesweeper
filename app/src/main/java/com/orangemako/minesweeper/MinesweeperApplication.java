package com.orangemako.minesweeper;

import android.app.Application;

import com.squareup.otto.Bus;

public class MinesweeperApplication extends Application {
    private static Bus gameBus;

    @Override
    public void onCreate() {
    }

    public static Bus getGameBus() {
        if(gameBus == null) {
            gameBus = new Bus();
        }

        return gameBus;
    }
}
