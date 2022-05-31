package com.example.tictactoe.controller;

import com.example.tictactoe.Exception.InvalidGame;
import com.example.tictactoe.Exception.InvalidParamException;
import com.example.tictactoe.Exception.NotFoundException;
import com.example.tictactoe.Storage.GameStorage;
import com.example.tictactoe.controller.dto.ConnectRequest;
import com.example.tictactoe.model.Game;
import com.example.tictactoe.model.GamePlay;
import com.example.tictactoe.model.GameStatus;
import com.example.tictactoe.model.Player;
import com.example.tictactoe.service.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Controller
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/")
    @ResponseBody
    public ResponseEntity<Game> start(@RequestBody Player player) {
        Game game = gameService.createGame(player);
        HttpHeaders responseHeaders = new HttpHeaders();
        URI newPollUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{gameId}")
                .buildAndExpand(game.getGameId())
                .toUri();
        responseHeaders.setLocation(newPollUri);
        return new ResponseEntity<>(game, responseHeaders, HttpStatus.CREATED);
    }
    @PostMapping("/{gameId}")
    @ResponseBody
    public ResponseEntity<Game> connect(@RequestBody Player player, @PathVariable String gameId) throws InvalidParamException, InvalidGame, NotFoundException {
        if(gameId.equals("random")) {
            Game game = GameStorage.getInstance().getGames().values().stream()
                    .filter(it -> it.getStatus().equals(GameStatus.NEW))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Game Not Found"));
            gameId = game.getGameId();
        }
        return ResponseEntity.ok(gameService.connectToGame(player, gameId));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Game> connectAnon(@PathVariable String gameId) throws InvalidParamException, InvalidGame {
        Player player = new Player();
        player.setLogin("Anonymous");
        return ResponseEntity.ok(gameService.connectToGame(player, gameId));
    }

    @PostMapping("/connect/random")
    public String connectRandom(@RequestBody Player player) throws NotFoundException {
        Game game = GameStorage.getInstance().getGames().values().stream()
                .filter(it -> it.getStatus().equals(GameStatus.NEW))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Game Not Found"));
        return "redirect:/game/" + game.getGameId();
    }

    @PostMapping("/gameplay")
    public ResponseEntity<Game>  gamePlay(@RequestBody GamePlay request) throws NotFoundException, InvalidGame {
        Game game = gameService.gamePlay(request);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/"+game.getGameId(), game);
        return ResponseEntity.ok(game);

    }



}
