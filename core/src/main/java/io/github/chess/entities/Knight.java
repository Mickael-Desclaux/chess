package io.github.chess.entities;

import io.github.chess.enums.PieceColor;

public class Knight extends Piece{

    public Knight(PieceColor pieceColor, Position position, Game game) {
        super(pieceColor, position, game);
    }

    @Override
    public boolean isValidMove(Position newPosition, Piece[][] board) {
        if (newPosition.equals(this.position)) {
            return false; // Cannot move to the same position
        }

        int rowDiff = Math.abs(this.position.getRow() - newPosition.getRow());
        int colDiff = Math.abs(this.position.getColumn() - newPosition.getColumn());

        // Check for the 'L' shaped move pattern
        boolean isValidLMove = (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);

        if (!isValidLMove) {
            return false; // Not a valid knight move
        }

        // Move is valid if the destination square is empty or contains an opponent's piece
        Piece targetPiece = board[newPosition.getRow()][newPosition.getColumn()];
        if (targetPiece == null) {
            return true; // The square is empty, move is valid
        } else {
            return targetPiece.getColor() != this.getColor(); // Can capture if it's an opponent's piece
        }
    }
}
