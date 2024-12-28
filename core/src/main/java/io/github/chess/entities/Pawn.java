package io.github.chess.entities;

import io.github.chess.enums.PieceColor;

public class Pawn extends Piece {

    public Pawn(PieceColor pieceColor, Position position) {
        super(pieceColor, position);
    }

    @Override
    public boolean isValidMove(Position newPosition, Piece[][] board) {
        int forwardDirection = pieceColor == PieceColor.WHITE ? -1 : 1;
        int rowDiff = (newPosition.getRow() - position.getRow()) * forwardDirection;
        int colorDiff = newPosition.getColumn() - position.getColumn();

        //Forward move
        if (colorDiff == 0 && rowDiff == 1 && board[newPosition.getRow()][newPosition.getColumn()] == null) {
            return true;
        }

        boolean isStartingPosition = (pieceColor == PieceColor.WHITE && position.getRow() == 6) ||
            (pieceColor == PieceColor.BLACK && position.getRow() == 1);
        if (colorDiff == 0 && rowDiff == 2 && isStartingPosition
            && board[newPosition.getRow()][newPosition.getColumn()] == null) {
            // Check the square in between for blocking pieces
            int middleRow = position.getRow() + forwardDirection;
            if (board[middleRow][position.getColumn()] == null) {
                return true; // Move forward two squares
            }
        }

        if (Math.abs(colorDiff) == 1 && rowDiff == 1 && board[newPosition.getRow()][newPosition.getColumn()] != null &&
            board[newPosition.getRow()][newPosition.getColumn()].pieceColor != this.pieceColor) {
            return true; // Capture an opponent's piece
        }

        return false;
    }
}
