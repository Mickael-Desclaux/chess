package io.github.chess.entities;

import io.github.chess.enums.PieceColor;

public abstract class Piece {
    protected Position position;
    protected PieceColor pieceColor;

    public Piece(PieceColor pieceColor, Position position) {
        this.pieceColor = pieceColor;
        this.position = position;
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
