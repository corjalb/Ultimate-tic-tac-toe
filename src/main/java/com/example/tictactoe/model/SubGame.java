package com.example.tictactoe.model;

import lombok.Data;

@Data
public class SubGame {
    private SubGameStatus status;
    private int[][] subBoard;
    private TicToe winner;
}
