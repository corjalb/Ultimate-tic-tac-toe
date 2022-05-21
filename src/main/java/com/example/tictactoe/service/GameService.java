package com.example.tictactoe.service;

import com.example.tictactoe.Exception.InvalidGame;
import com.example.tictactoe.Exception.InvalidParamException;
import com.example.tictactoe.Exception.NotFoundException;
import com.example.tictactoe.Storage.GameStorage;
import com.example.tictactoe.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GameService {
    public Game createGame(Player player) {
        Game game = new Game();
        for(int i = 0; i < 9; i++) {
            SubGame subGame = new SubGame();
            subGame.setSubBoard(new int[3][3]);
            subGame.setStatus(SubGameStatus.ACTIVE);
            game.addToBoard(subGame);
        }
        game.setGameId(UUID.randomUUID().toString());
        game.setPlayer1(player);
        game.setStatus(GameStatus.NEW);
        game.setTurn(TicToe.X);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game connectToGame(Player player2, String gameId) throws InvalidParamException, InvalidGame {
        if(!GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new InvalidParamException("Game with provided ID doesn't exist");
        }
        Game game = GameStorage.getInstance().getGames().get(gameId);
        if(game.getPlayer2() != null) {
            throw new InvalidGame("Game is not valid");
        }
        game.setPlayer2(player2);
        game.setStatus(GameStatus.IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game connectToRandomGame(Player player2) throws NotFoundException {
        Game game = GameStorage.getInstance().getGames().values().stream()
                .filter(it -> it.getStatus().equals(GameStatus.NEW))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Game Not Found"));
        game.setPlayer2(player2);
        game.setStatus(GameStatus.IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGame {
        if(!GameStorage.getInstance().getGames().containsKey(gamePlay.getGameId())) {
            throw new NotFoundException("Game not found");
        }
        Game game = GameStorage.getInstance().getGames().get(gamePlay.getGameId());
        if(game.getStatus() == GameStatus.FINISHED) {
            throw new InvalidGame("Game is already finished");
        }
        if(game.getPlayer2() == null) {
            throw new InvalidGame("Player 2 has not joined yet");
        }
        List<SubGame> board = game.getBoard();
        SubGame subGame = board.get(gamePlay.getSubBoardNumber());
        int[][] subBoard = board.get(gamePlay.getSubBoardNumber()).getSubBoard();
        subBoard[gamePlay.getCoordinateX()][gamePlay.getCoordinateY()] = gamePlay.getType().getValue();
        int newSubBoard = gamePlay.getCoordinateX() * 3 + gamePlay.getCoordinateY();

        if(board.get(newSubBoard).getStatus() == SubGameStatus.WON || board.get(newSubBoard).getStatus() == SubGameStatus.STALE) {
            activateAllSubBoards(game);
        } else {
            activateOneSubBoard(game, newSubBoard);
        }

        if(checkSubWinner(subBoard, gamePlay.getType())) {
            subGame.setStatus(SubGameStatus.WON);
            subGame.setWinner(gamePlay.getType());
        }
        if(checkStale(subBoard)) {
            subGame.setStatus(SubGameStatus.STALE);
        }

        if(checkWinner(game.getBoard(), gamePlay.getType())) {
            game.setStatus(GameStatus.FINISHED);
            game.setWinner(gamePlay.getType());
        }
        changeTurn(game);

        GameStorage.getInstance().setGame(game);
        return game;
    }

    private Boolean checkWinner(List<SubGame> board, TicToe ticToe) {
        int[][] winCombinations = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},
                {1,4,7},{2,5,8},{0,4,8},{2,4,6}};
        for(int[] win : winCombinations) {
            int counter = 0;
            for(int i = 0; i < win.length; i++) {
                if(board.get(win[i]).getWinner() == ticToe) counter++;
                if(counter == 3) return true;
            }
        }
        return false;
    }

    private Boolean checkSubWinner(int[][] subBoard, TicToe ticToe) {
        int[] boardArray = new int[9];
        int counterIndex = 0;
        for(int i = 0; i < subBoard.length; i++) {
            for(int j = 0; j < subBoard[i].length; j++) {
                boardArray[counterIndex++] = subBoard[i][j];
            }
        }
        int[][] winCombinations = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},
                {1,4,7},{2,5,8},{0,4,8},{2,4,6}};
        for(int[] win : winCombinations) {
            int counter = 0;
            for(int i = 0; i < win.length; i++) {
                if(boardArray[win[i]] == ticToe.getValue()) counter++;
                if(counter == 3) return true;
            }
        }
        return false;
    }

    private Boolean checkStale(int[][] subBoard) {
        for (int i = 0; i < subBoard.length; i++) {
            for (int j = 0; j < subBoard[i].length; j++) {
                if(subBoard[i][j] != 1 && subBoard[i][j] != 2) return false;
            }
        }
        return true;
    }

    private void changeTurn(Game game) {
        if(game.getTurn() == TicToe.X) game.setTurn(TicToe.O);
        else game.setTurn(TicToe.X);
    }

    private void activateAllSubBoards(Game game) {
        for(SubGame subGame : game.getBoard()) {
            if(subGame.getStatus() != SubGameStatus.STALE && subGame.getStatus() != SubGameStatus.WON) {
                subGame.setStatus(SubGameStatus.ACTIVE);
            }
        }
    }

    private void activateOneSubBoard(Game game, int subBoardNumber) {
        for(SubGame subGame : game.getBoard()) {
            if(subGame.getStatus() != SubGameStatus.STALE && subGame.getStatus() != SubGameStatus.WON) {
                subGame.setStatus(SubGameStatus.INACTIVE);
            }
        }
        game.getBoard().get(subBoardNumber).setStatus(SubGameStatus.ACTIVE);
    }
}
