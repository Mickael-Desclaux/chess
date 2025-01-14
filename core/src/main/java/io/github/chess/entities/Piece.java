package io.github.chess.entities;

import io.github.chess.enums.PieceColor;

public abstract class Piece {
    protected Position position;
    protected PieceColor pieceColor;
    protected Game game;

    public Piece(PieceColor pieceColor, Position position, Game game) {
        this.pieceColor = pieceColor;
        this.position = position;
        this.game = game;
    }

    public PieceColor getColor() {
        return pieceColor;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public abstract boolean isValidMove(Position newPosition, Piece[][] board);
}
