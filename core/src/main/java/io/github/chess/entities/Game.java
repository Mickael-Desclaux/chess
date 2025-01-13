package io.github.chess.entities;

import io.github.chess.enums.PieceColor;

public class Game {

    private Board board;
    private boolean whiteTurn = true;
    private Position whiteKingPosition;
    private Position blackKingPosition;

    public Game() {
        this.board = new Board();
        this.whiteTurn = true;
        updateKingPositions();
    }

    public boolean movePiece(Position from, Position to) {
        Piece piece = board.getBoard()[from.getRow()][from.getColumn()];

        if (piece == null || (whiteTurn && piece.getColor() != PieceColor.WHITE) ||
            (!whiteTurn && piece.getColor() != PieceColor.BLACK)) {
            return false;
        }

        // Vérifier si le mouvement est valide et ne met pas son propre roi en échec
        if (canMoveWithoutCheck(piece, to)) {
            board.movePiece(from, to);
            updateKingPositions();

            // Vérifier si ce mouvement met l'adversaire en échec
            PieceColor oppositeColor = piece.getColor() == PieceColor.WHITE ?
                PieceColor.BLACK : PieceColor.WHITE;
            if (isInCheck(oppositeColor)) {
                if (isCheckmate(oppositeColor)) {
                    System.out.println("Échec et mat !");
                } else {
                    System.out.println("Échec !");
                }
            }

            whiteTurn = !whiteTurn;
            return true;
        }
        return false;
    }

    private void updateKingPositions() {
        Piece[][] pieces = board.getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = pieces[row][col];
                if (piece instanceof King) {
                    if (piece.getColor() == PieceColor.WHITE) {
                        whiteKingPosition = new Position(row, col);
                    } else {
                        blackKingPosition = new Position(row, col);
                    }
                }
            }
        }
    }

    public boolean isInCheck(PieceColor kingColor) {
        Position kingPosition = (kingColor == PieceColor.WHITE) ? whiteKingPosition : blackKingPosition;
        Piece[][] pieces = board.getBoard();

        // Vérifier si une pièce adverse peut atteindre le roi
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = pieces[row][col];
                if (piece != null && piece.getColor() != kingColor) {
                    if (piece.isValidMove(kingPosition, pieces)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isCheckmate(PieceColor kingColor) {
        if (!isInCheck(kingColor)) {
            return false;
        }

        // Vérifier tous les mouvements possibles pour toutes les pièces du joueur
        Piece[][] pieces = board.getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = pieces[row][col];
                if (piece != null && piece.getColor() == kingColor) {
                    // Tester tous les mouvements possibles pour cette pièce
                    for (int newRow = 0; newRow < 8; newRow++) {
                        for (int newCol = 0; newCol < 8; newCol++) {
                            Position newPos = new Position(newRow, newCol);
                            if (canMoveWithoutCheck(piece, newPos)) {
                                return false; // Il existe au moins un mouvement valide
                            }
                        }
                    }
                }
            }
        }
        return true; // Aucun mouvement valide trouvé
    }

    private boolean canMoveWithoutCheck(Piece piece, Position newPos) {
        // Sauvegarder l'état actuel
        Position originalPos = piece.getPosition();
        Piece capturedPiece = board.getBoard()[newPos.getRow()][newPos.getColumn()];

        if (piece.isValidMove(newPos, board.getBoard())) {
            // Faire le mouvement temporairement
            board.movePiece(originalPos, newPos);
            updateKingPositions();

            // Vérifier si le roi est toujours en échec
            boolean stillInCheck = isInCheck(piece.getColor());

            // Annuler le mouvement
            board.getBoard()[originalPos.getRow()][originalPos.getColumn()] = piece;
            board.getBoard()[newPos.getRow()][newPos.getColumn()] = capturedPiece;
            piece.setPosition(originalPos);
            updateKingPositions();

            return !stillInCheck;
        }
        return false;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public Position getWhiteKingPosition() {
        return whiteKingPosition;
    }

    public Position getBlackKingPosition() {
        return blackKingPosition;
    }
}
