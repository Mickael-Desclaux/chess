package io.github.chess.entities;

public class Game {

    private Board board;
    private boolean whiteTurn = true;

    public Game() {
        this.board = new Board();
    }
}
