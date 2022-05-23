package com.example.tictactoe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/")
    public String login() {
        return "index";
    }

    @GetMapping("/game-board")
    public String gameBoard() {
        return "game";
    }
}
