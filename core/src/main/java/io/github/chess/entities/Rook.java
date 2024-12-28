package io.github.chess.entities;

import io.github.chess.enums.PieceColor;

public class Rook extends Piece{

    public Rook(PieceColor pieceColor, Position position) {
        super(pieceColor, position);
    }

    @Override
    public boolean isValidMove(Position newPosition, Piece[][] board) {
        if (position.getRow() == newPosition.getRow()) {
            int columnStart = Math.min(position.getColumn(), newPosition.getColumn()) + 1;
            int columnEnd = Math.max(position.getColumn(), newPosition.getColumn());
            for (int column = columnStart; column < columnEnd; column++) {
                if (board[position.getRow()][column] != null) {
                    return false; // There's a piece in the way
                }
            }
        } else if (position.getColumn() == newPosition.getColumn()) {
            int rowStart = Math.min(position.getRow(), newPosition.getRow()) + 1;
            int rowEnd = Math.max(position.getRow(), newPosition.getRow());
            for (int row = rowStart; row < rowEnd; row++) {
                if (board[row][position.getColumn()] != null) {
                    return false; // There's a piece in the way
                }
            }
        } else {
            return false; // Not a valid rook move (not straight line)
        }

        Piece destinationPiece = board[newPosition.getRow()][newPosition.getColumn()];
        if (destinationPiece == null) {
            return true; // The destination is empty, move is valid.
        } else if (destinationPiece.getColor() != this.getColor()) {
            return true; // The destination has an opponent's piece, capture is valid.
        }

        return false; // The destination has a piece of the same color, move is invalid.
    }
}
