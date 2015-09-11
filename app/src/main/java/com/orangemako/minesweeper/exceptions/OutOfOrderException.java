package com.orangemako.minesweeper.exceptions;

public class OutOfOrderException extends Exception {
    public OutOfOrderException() { super(); }

    public OutOfOrderException(String detailMessage) {
        super(detailMessage);
    }
}
