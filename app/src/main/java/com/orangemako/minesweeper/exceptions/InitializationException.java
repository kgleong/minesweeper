package com.orangemako.minesweeper.exceptions;

public class InitializationException extends Exception {
    public InitializationException() {
        super();
    }

    public InitializationException(String detailMessage) {
        super(detailMessage);
    }
}
