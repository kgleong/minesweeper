package com.orangemako.minesweeper;

import android.app.Application;

import com.orangemako.minesweeper.utilities.NetworkUtils;
import com.squareup.otto.Bus;

public class MainApplication extends Application {
    private static Bus gameBus;
    private static MainApplication mApplicationInstance;

    public static Bus getGameBus() {
        if(gameBus == null) {
            gameBus = new Bus();
        }

        return gameBus;
    }

    public static boolean isNetworkAvailable() {
        return NetworkUtils.isNetworkAvailable(mApplicationInstance);
    }

    @Override
    public void onCreate() {
        mApplicationInstance = thisttr;
    }
}
