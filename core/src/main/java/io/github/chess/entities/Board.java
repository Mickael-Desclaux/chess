package io.github.chess.entities;

public class Board {
    private Piece[][] board;

    public Board() {
        this.board = new Piece[8][8];
    }

    public Piece[][] getBoard() {
        return board;
    }

    public void movePiece(Position start, Position end) {
        try {
            Piece piece = board[start.getRow()][start.getColumn()];
            if (piece != null) {
                board[start.getRow()][start.getColumn()] = null;
                board[end.getRow()][end.getColumn()] = piece;
                piece.setPosition(end);
                System.out.println("Piece moved from " + start + " to " + end);
            } else {
                System.out.println("No piece found at start position: " + start);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while moving the piece: " + e.getMessage());
        }
    }
}
