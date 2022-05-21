package com.example.tictactoe.model;

import lombok.Data;

@Data
public class GamePlay {
    private TicToe type;
    private Integer coordinateX;
    private Integer coordinateY;
    private String gameId;

    private Integer subBoardNumber;

    public String getGameId() {
        return gameId;
    }

    public Integer getCoordinateX() {
        return coordinateX;
    }

    public Integer getCoordinateY() {
        return coordinateY;
    }

    public TicToe getType() {
        return type;
    }
}
