package com.example.tictactoe.controller;

import com.example.tictactoe.Exception.InvalidGame;
import com.example.tictactoe.Exception.InvalidParamException;
import com.example.tictactoe.Exception.NotFoundException;
import com.example.tictactoe.controller.dto.ConnectRequest;
import com.example.tictactoe.model.Game;
import com.example.tictactoe.model.GamePlay;
import com.example.tictactoe.model.Player;
import com.example.tictactoe.service.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/start")
    public ResponseEntity<Game> start(@RequestBody Player player) {
        return ResponseEntity.ok(gameService.createGame(player));
    }
    @PostMapping("/connect")
    public ResponseEntity<Game> connect(@RequestBody ConnectRequest request) throws InvalidParamException, InvalidGame {
        return ResponseEntity.ok(gameService.connectToGame(request.getPlayer(), request.getGameId()));
    }

    @PostMapping("/connect/random")
    public ResponseEntity<Game> connectRandom(@RequestBody Player player) throws NotFoundException {
        return ResponseEntity.ok(gameService.connectToRandomGame(player));
    }

    @PostMapping("/gameplay")
    public ResponseEntity<Game>  gamePlay(@RequestBody GamePlay request) throws NotFoundException, InvalidGame {
        Game game = gameService.gamePlay(request);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/"+game.getGameId(), game);
        return ResponseEntity.ok(game);

    }

    /*@GetMapping("/")
    public String login() {
        return "index";
    }*/

    @GetMapping("/game-board")
    public String gameBoard() {
        return "game";
    }
}
