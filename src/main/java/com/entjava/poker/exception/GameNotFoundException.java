// GameNotFoundException.java
package com.entjava.poker.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String message) {
        super(message);
    }
}