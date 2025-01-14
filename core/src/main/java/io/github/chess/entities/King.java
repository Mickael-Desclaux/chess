package io.github.chess.entities;

import io.github.chess.enums.PieceColor;

public class King extends Piece {

    public King(PieceColor pieceColor, Position position, Game game) {
        super(pieceColor, position, game);
    }

    @Override
    public boolean isValidMove(Position newPosition, Piece[][] board) {
        int rowDiff = Math.abs(position.getRow() - newPosition.getRow());
        int colDiff = Math.abs(position.getColumn() - newPosition.getColumn());

        // Kings can move one square in any direction.
        boolean isOneSquareMove = rowDiff <= 1 && colDiff <= 1 && !(rowDiff == 0 && colDiff == 0);

        if (!isOneSquareMove) {
            return false; // Move is not within one square.
        }

        Piece destinationPiece = board[newPosition.getRow()][newPosition.getColumn()];
        // The move is valid if the destination is empty or contains an opponent's piece.
        return destinationPiece == null || destinationPiece.getColor() != this.getColor();
    }
}
