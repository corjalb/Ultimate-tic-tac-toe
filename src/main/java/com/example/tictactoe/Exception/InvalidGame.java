package com.example.tictactoe.Exception;

public class InvalidGame extends Exception{
    private String message;
    public InvalidGame(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
