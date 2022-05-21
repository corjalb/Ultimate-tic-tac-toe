package com.example.tictactoe.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TicToe {
    X(1), O(2);
    private Integer value;
    TicToe(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
