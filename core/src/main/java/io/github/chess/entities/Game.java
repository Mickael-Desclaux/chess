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
    private boolean whiteKingMoved = false;
    private boolean blackKingMoved = false;
    private boolean whiteRookAMoved = false;
    private boolean whiteRookHMoved = false;
    private boolean blackRookAMoved = false;
    private boolean blackRookHMoved = false;

    public Game() {
        this.board = new Board();
        initializeBoard();
        updateKingPositions();
    }

    private void initializeBoard() {
        Piece[][] pieces = board.getBoard();

        // Initializing pieces
        for (int col = 0; col < 8; col++) {
            pieces[1][col] = new Pawn(PieceColor.BLACK, new Position(1, col), this);
            pieces[6][col] = new Pawn(PieceColor.WHITE, new Position(6, col), this);
        }

        pieces[0][0] = new Rook(PieceColor.BLACK, new Position(0, 0), this);
        pieces[0][7] = new Rook(PieceColor.BLACK, new Position(0, 7), this);
        pieces[7][0] = new Rook(PieceColor.WHITE, new Position(7, 0), this);
        pieces[7][7] = new Rook(PieceColor.WHITE, new Position(7, 7), this);

        pieces[0][1] = new Knight(PieceColor.BLACK, new Position(0, 1), this);
        pieces[0][6] = new Knight(PieceColor.BLACK, new Position(0, 6), this);
        pieces[7][1] = new Knight(PieceColor.WHITE, new Position(7, 1), this);
        pieces[7][6] = new Knight(PieceColor.WHITE, new Position(7, 6), this);

        pieces[0][2] = new Bishop(PieceColor.BLACK, new Position(0, 2), this);
        pieces[0][5] = new Bishop(PieceColor.BLACK, new Position(0, 5), this);
        pieces[7][2] = new Bishop(PieceColor.WHITE, new Position(7, 2), this);
        pieces[7][5] = new Bishop(PieceColor.WHITE, new Position(7, 5), this);

        pieces[0][3] = new Queen(PieceColor.BLACK, new Position(0, 3), this);
        pieces[7][3] = new Queen(PieceColor.WHITE, new Position(7, 3), this);

        pieces[0][4] = new King(PieceColor.BLACK, new Position(0, 4), this);
        pieces[7][4] = new King(PieceColor.WHITE, new Position(7, 4), this);
    }

    public boolean movePiece(Position from, Position to) {
        Piece piece = board.getBoard()[from.getRow()][from.getColumn()];

        if (piece == null || (whiteTurn && piece.getColor() != PieceColor.WHITE) ||
            (!whiteTurn && piece.getColor() != PieceColor.BLACK)) {
            return false;
        }

        if (piece instanceof King) {
            if (piece.getColor() == PieceColor.WHITE) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
        } else if (piece instanceof Rook) {
            if (piece.getColor() == PieceColor.WHITE) {
                if (from.equals(new Position(7, 0))) {
                    whiteRookAMoved = true;
                } else if (from.equals(new Position(7, 7))) {
                    whiteRookHMoved = true;
                }
            } else {
                if (from.equals(new Position(0, 0))) {
                    blackRookAMoved = true;
                } else if (from.equals(new Position(0, 7))) {
                    blackRookHMoved = true;
                }
            }
        }

        if (piece instanceof King) {
            int colDiff = to.getColumn() - from.getColumn();
            if (Math.abs(colDiff) == 2) {
                boolean isKingSide = colDiff > 0;
                return castle(piece.getColor(), isKingSide);
            }
        }

        try {
            if (canMoveWithoutCheck(piece, to)) {
                if (piece instanceof Pawn) {
                    handleEnPassant(piece, from, to);
                } else {
                    enPassantTarget = null;
                    lastPawnDoubleMove = null;
                }

                boolean isCapture = board.getBoard()[to.getRow()][to.getColumn()] != null;
                String notation = board.getAlgebraicNotation(piece, from, to, isCapture);

                board.movePiece(from, to);
                System.out.println(notation);
                updateKingPositions();

                // Check for pawn promotion
                if (piece instanceof Pawn && isPromotion(piece, to)) {
                    promotionPosition = to;
                    promotionColor = piece.getColor();
                    isPromoting = true;
                    waitingForPromotionSelection = true;
                    return false;
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

        // If a pawn moves from its 2nd to 4rth row
        if (rowDiff == 2) {
            enPassantTarget = new Position(
                (from.getRow() + to.getRow()) / 2,
                to.getColumn()
            );
            lastPawnDoubleMove = to;
        }
        else if (to.equals(enPassantTarget)) {
            board.getBoard()[lastPawnDoubleMove.getRow()][lastPawnDoubleMove.getColumn()] = null;
        }
        else {
            enPassantTarget = null;
            lastPawnDoubleMove = null;
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

        // Check for all possible moves
        Piece[][] pieces = board.getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = pieces[row][col];
                if (piece != null && piece.getColor() == kingColor) {
                    for (int newRow = 0; newRow < 8; newRow++) {
                        for (int newCol = 0; newCol < 8; newCol++) {
                            Position newPos = new Position(newRow, newCol);
                            if (canMoveWithoutCheck(piece, newPos)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean canMoveWithoutCheck(Piece piece, Position newPos) {
        Position originalPos = piece.getPosition();
        Piece capturedPiece = board.getBoard()[newPos.getRow()][newPos.getColumn()];

        if (piece.isValidMove(newPos, board.getBoard())) {
            board.movePiece(originalPos, newPos);
            updateKingPositions();

            // Check if the king is still in check
            boolean stillInCheck = isInCheck(piece.getColor());

            board.getBoard()[originalPos.getRow()][originalPos.getColumn()] = piece;
            board.getBoard()[newPos.getRow()][newPos.getColumn()] = capturedPiece;
            piece.setPosition(originalPos);
            updateKingPositions();

            return !stillInCheck;
        }
        return false;
    }

    public boolean isValidMoveInCheck(Piece piece, Position newPos) {
        Position originalPos = piece.getPosition();
        Piece capturedPiece = board.getBoard()[newPos.getRow()][newPos.getColumn()];

        if (piece.isValidMove(newPos, board.getBoard())) {
            board.movePiece(originalPos, newPos);
            updateKingPositions();

            boolean stillInCheck = isInCheck(piece.getColor());

            board.getBoard()[originalPos.getRow()][originalPos.getColumn()] = piece;
            board.getBoard()[newPos.getRow()][newPos.getColumn()] = capturedPiece;
            piece.setPosition(originalPos);
            updateKingPositions();

            return !stillInCheck;
        }
        return false;
    }

    private boolean canCastle(PieceColor color, boolean isKingSide) {
        Position kingPosition = (color == PieceColor.WHITE) ? whiteKingPosition : blackKingPosition;
        Position rookPosition = isKingSide ? new Position(kingPosition.getRow(), 7) : new Position(kingPosition.getRow(), 0);
        boolean kingMoved = (color == PieceColor.WHITE) ? whiteKingMoved : blackKingMoved;
        boolean rookMoved = isKingSide ? (color == PieceColor.WHITE ? whiteRookHMoved : blackRookHMoved) : (color == PieceColor.WHITE ? whiteRookAMoved : blackRookAMoved);

        if (kingMoved || rookMoved) {
            return false;
        }

        int direction = isKingSide ? 1 : -1;
        int startCol = isKingSide ? kingPosition.getColumn() + 1 : kingPosition.getColumn() - 1;
        int endCol = isKingSide ? 6 : 1;

        for (int col = startCol; col != endCol; col += direction) {
            Position pos = new Position(kingPosition.getRow(), col);
            if (board.getBoard()[pos.getRow()][pos.getColumn()] != null || isInCheckAfterMove(kingPosition, pos)) {
                return false;
            }
        }

        return !isInCheck(color);
    }

    public boolean castle(PieceColor color, boolean isKingSide) {
        if (!canCastle(color, isKingSide)) {
            return false;
        }

        Position kingPosition = (color == PieceColor.WHITE) ? whiteKingPosition : blackKingPosition;
        Position rookPosition = isKingSide ? new Position(kingPosition.getRow(), 7) : new Position(kingPosition.getRow(), 0);
        Position newKingPosition = isKingSide ? new Position(kingPosition.getRow(), 6) : new Position(kingPosition.getRow(), 2);
        Position newRookPosition = isKingSide ? new Position(kingPosition.getRow(), 5) : new Position(kingPosition.getRow(), 3);

        board.movePiece(kingPosition, newKingPosition);
        board.movePiece(rookPosition, newRookPosition);

        updateKingPositions();
        whiteTurn = !whiteTurn;
        return true;
    }


    private boolean isInCheckAfterMove(Position from, Position to) {
        Piece piece = board.getBoard()[from.getRow()][from.getColumn()];
        Piece capturedPiece = board.getBoard()[to.getRow()][to.getColumn()];

        board.movePiece(from, to);
        boolean inCheck = isInCheck(piece.getColor());
        board.movePiece(to, from);
        board.getBoard()[to.getRow()][to.getColumn()] = capturedPiece;

        return inCheck;
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
