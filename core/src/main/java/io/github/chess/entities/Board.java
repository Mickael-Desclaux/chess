package io.github.chess.entities;

public class Board {
    private Piece[][] board;
    private static final String[] FILES = {"a", "b", "c", "d", "e", "f", "g", "h"};

    public Board() {
        this.board = new Piece[8][8];
    }

    public Piece[][] getBoard() {
        return board;
    }

    protected String getAlgebraicNotation(Piece piece, Position start, Position end, boolean isCapture) {
        if (piece instanceof Pawn) {
            if (isCapture) {
                return FILES[start.getColumn()] + "x" + FILES[end.getColumn()] + (8 - end.getRow());
            }
            return FILES[end.getColumn()] + (8 - end.getRow());
        }

        String pieceNotation = "";
        if (piece instanceof King) pieceNotation = "K";
        else if (piece instanceof Queen) pieceNotation = "Q";
        else if (piece instanceof Rook) pieceNotation = "R";
        else if (piece instanceof Bishop) pieceNotation = "B";
        else if (piece instanceof Knight) pieceNotation = "N";

        return pieceNotation + (isCapture ? "x" : "") +
            FILES[end.getColumn()] + (8 - end.getRow());
    }

    public void movePiece(Position start, Position end) {
        try {
            Piece piece = board[start.getRow()][start.getColumn()];
            if (piece != null) {
                board[start.getRow()][start.getColumn()] = null;
                board[end.getRow()][end.getColumn()] = piece;
                piece.setPosition(end);
            } else {
                System.out.println("No piece found at start position: " + start);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while moving the piece: " + e.getMessage());
        }
    }
}
