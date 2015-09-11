package com.orangemako.minesweeper.exceptions;

public class InvalidArgumentException extends Exception {
    public InvalidArgumentException() {
        super();
    }

    public InvalidArgumentException(String detailMessage) {
        super(detailMessage);
    }
}
