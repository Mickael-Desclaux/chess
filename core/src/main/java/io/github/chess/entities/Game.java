package io.github.chess.entities;

import io.github.chess.enums.PieceColor;

public class Game {

    private final Board board;
    private boolean whiteTurn = true;
    private Position whiteKingPosition;
    private Position blackKingPosition;
    private Position enPassantTarget;
    private Position lastPawnDoubleMove;
    private Position promotionPosition;
    private PieceColor promotionColor;
    private boolean isPromoting = false;
    private boolean waitingForPromotionSelection = false;

    public Game() {
        this.board = new Board();
        initializeBoard();
        updateKingPositions();
    }

    private void initializeBoard() {
        Piece[][] pieces = board.getBoard();

        // Initialiser les pions
        for (int col = 0; col < 8; col++) {
            pieces[1][col] = new Pawn(PieceColor.BLACK, new Position(1, col), this);
            pieces[6][col] = new Pawn(PieceColor.WHITE, new Position(6, col), this);
        }

        // Initialiser les autres pièces
        // Tours
        pieces[0][0] = new Rook(PieceColor.BLACK, new Position(0, 0), this);
        pieces[0][7] = new Rook(PieceColor.BLACK, new Position(0, 7), this);
        pieces[7][0] = new Rook(PieceColor.WHITE, new Position(7, 0), this);
        pieces[7][7] = new Rook(PieceColor.WHITE, new Position(7, 7), this);

        // Cavaliers
        pieces[0][1] = new Knight(PieceColor.BLACK, new Position(0, 1), this);
        pieces[0][6] = new Knight(PieceColor.BLACK, new Position(0, 6), this);
        pieces[7][1] = new Knight(PieceColor.WHITE, new Position(7, 1), this);
        pieces[7][6] = new Knight(PieceColor.WHITE, new Position(7, 6), this);

        // Fous
        pieces[0][2] = new Bishop(PieceColor.BLACK, new Position(0, 2), this);
        pieces[0][5] = new Bishop(PieceColor.BLACK, new Position(0, 5), this);
        pieces[7][2] = new Bishop(PieceColor.WHITE, new Position(7, 2), this);
        pieces[7][5] = new Bishop(PieceColor.WHITE, new Position(7, 5), this);

        // Reines
        pieces[0][3] = new Queen(PieceColor.BLACK, new Position(0, 3), this);
        pieces[7][3] = new Queen(PieceColor.WHITE, new Position(7, 3), this);

        // Rois
        pieces[0][4] = new King(PieceColor.BLACK, new Position(0, 4), this);
        pieces[7][4] = new King(PieceColor.WHITE, new Position(7, 4), this);
    }

    public boolean movePiece(Position from, Position to) {
        System.out.println(enPassantTarget);
        Piece piece = board.getBoard()[from.getRow()][from.getColumn()];

        if (piece == null || (whiteTurn && piece.getColor() != PieceColor.WHITE) ||
            (!whiteTurn && piece.getColor() != PieceColor.BLACK)) {
            return false;
        }

        try {
            if (canMoveWithoutCheck(piece, to)) {
                // Gestion de la prise en passant
                if (piece instanceof Pawn) {
                    handleEnPassant(piece, from, to);
                } else {
                    // Réinitialiser la cible de prise en passant si une autre pièce bouge
                    enPassantTarget = null;
                    lastPawnDoubleMove = null;
                }

                // Déplacer la pièce
                board.movePiece(from, to);
                updateKingPositions();

                // Vérifier la promotion du pion
                if (piece instanceof Pawn && isPromotion(piece, to)) {
                    promotionPosition = to;
                    promotionColor = piece.getColor();
                    isPromoting = true;
                    waitingForPromotionSelection = true;
                    return false; // Ne pas changer de tour tant que la promotion n'est pas terminée
                }

                whiteTurn = !whiteTurn;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while moving the piece: " + e.getMessage());
        }
        return false;
    }

    private boolean isPromotion(Piece piece, Position to) {
        if (piece instanceof Pawn) {
            if (piece.getColor() == PieceColor.WHITE && to.getRow() == 0) {
                return true;
            }
            if (piece.getColor() == PieceColor.BLACK && to.getRow() == 7) {
                return true;
            }
        }
        return false;
    }

    private void handleEnPassant(Piece pawn, Position from, Position to) {
        int rowDiff = Math.abs(from.getRow() - to.getRow());

        // Si un pion avance de deux cases
        if (rowDiff == 2) {
            enPassantTarget = new Position(
                (from.getRow() + to.getRow()) / 2, // La case intermédiaire
                to.getColumn()
            );
            lastPawnDoubleMove = to;
            System.out.println("Double step detected: enPassantTarget = " + enPassantTarget);
        }
        // Si le mouvement est une prise en passant
        else if (to.equals(enPassantTarget)) {
            System.out.println("En passant capture detected: Target = " + enPassantTarget);
            board.getBoard()[lastPawnDoubleMove.getRow()][lastPawnDoubleMove.getColumn()] = null;
            // Ne pas déplacer le pion ici, cela sera fait dans movePiece
        }
        // Autre mouvement
        else {
            enPassantTarget = null;
            lastPawnDoubleMove = null;
            System.out.println("Resetting enPassantTarget and lastPawnDoubleMove");
        }
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

    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    public Position getWhiteKingPosition() {
        return whiteKingPosition;
    }

    public Position getBlackKingPosition() {
        return blackKingPosition;
    }

    public Position getEnPassantTarget() {
        return enPassantTarget;
    }

    public Position getLastPawnDoubleMove() {
        return lastPawnDoubleMove;
    }

    public Position getPromotionPosition() {
        return promotionPosition;
    }

    public PieceColor getPromotionColor() {
        return promotionColor;
    }

    public boolean isPromoting() {
        return isPromoting;
    }

    public void setPromoting(boolean promoting) {
        isPromoting = promoting;
    }

    public boolean isWaitingForPromotionSelection() {
        return waitingForPromotionSelection;
    }

    public void setWaitingForPromotionSelection(boolean waitingForPromotionSelection) {
        this.waitingForPromotionSelection = waitingForPromotionSelection;
    }
}
