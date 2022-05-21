package com.example.tictactoe.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Game {

    private String gameId;
    private Player player1;
    private Player player2;
    private GameStatus status;
    private List<SubGame> board = new ArrayList<>();
    private TicToe winner;

    private TicToe turn;

    public void setBoard(List<SubGame> board) {
        this.board = board;
    }

    public void addToBoard(SubGame subGame) { this.board.add(subGame);}

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void setWinner(TicToe winner) {
        this.winner = winner;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public String getGameId() {
        return gameId;
    }

    public GameStatus getStatus() {
        return status;
    }

    public TicToe getWinner() {
        return winner;
    }

    public List<SubGame> getBoard() {
        return board;
    }
}
