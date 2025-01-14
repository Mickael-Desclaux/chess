package io.github.chess.entities;

import io.github.chess.enums.PieceColor;

public class Pawn extends Piece {

    public Pawn(PieceColor pieceColor, Position position, Game game) {
        super(pieceColor, position, game);
    }

    @Override
    public boolean isValidMove(Position newPosition, Piece[][] board) {
        int forwardDirection = pieceColor == PieceColor.WHITE ? -1 : 1;
        int rowDiff = (newPosition.getRow() - position.getRow()) * forwardDirection;
        int columnDiff = newPosition.getColumn() - position.getColumn();

        // Vérification de la prise en passant
        if (Math.abs(columnDiff) == 1 && rowDiff == 1) {
            Position enPassantTarget = game.getEnPassantTarget();
            if (enPassantTarget != null && enPassantTarget.equals(newPosition)) {
                Position lastPawnMove = game.getLastPawnDoubleMove();
                if (lastPawnMove != null) {
                    Piece potentialPawn = board[lastPawnMove.getRow()][lastPawnMove.getColumn()];
                    if (potentialPawn != null &&
                        potentialPawn instanceof Pawn &&
                        potentialPawn.getColor() != this.pieceColor) {
                        System.out.println("En passant capture validated: Target = " + enPassantTarget);
                        return true;
                    }
                }
            }
        }

        // Mouvement en avant
        if (columnDiff == 0 && rowDiff == 1 && board[newPosition.getRow()][newPosition.getColumn()] == null) {
            return true;
        }

        boolean isStartingPosition = (pieceColor == PieceColor.WHITE && position.getRow() == 6) ||
            (pieceColor == PieceColor.BLACK && position.getRow() == 1);
        if (columnDiff == 0 && rowDiff == 2 && isStartingPosition &&
            board[newPosition.getRow()][newPosition.getColumn()] == null) {
            // Vérification de la case intermédiaire
            int middleRow = position.getRow() + forwardDirection;
            if (board[middleRow][position.getColumn()] == null) {
                return true; // Mouvement de deux cases en avant
            }
        }

        // Capture d'une pièce adverse
        if (Math.abs(columnDiff) == 1 && rowDiff == 1 && board[newPosition.getRow()][newPosition.getColumn()] != null &&
            board[newPosition.getRow()][newPosition.getColumn()].pieceColor != this.pieceColor) {
            return true;
        }

        return false;
    }
}
