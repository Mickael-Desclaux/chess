package io.github.chess;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.chess.entities.Game;
import io.github.chess.entities.Piece;
import io.github.chess.entities.Position;
import io.github.chess.enums.PieceColor;

import java.util.HashMap;
import java.util.Map;

public class Chess extends ApplicationAdapter {

    private static Map<String, Texture> pieceTextures;
    private Texture boardTexture;
    private SpriteBatch batch;
    private int boardSize = 480;
    private Position selectedPosition;
    private boolean isPieceSelected;
    private Game game;
    private Texture highlightTexture;

    @Override
    public void create() {
        game = new Game();
        batch = new SpriteBatch();

        boardTexture = new Texture("board/chess_board.png");

        // Load piece textures
        pieceTextures = new HashMap<>();
        // Load white pieces
        pieceTextures.put("WHITE_PAWN", new Texture("pieces/white/white_pawn.png"));
        pieceTextures.put("WHITE_ROOK", new Texture("pieces/white/white_rook.png"));
        pieceTextures.put("WHITE_KNIGHT", new Texture("pieces/white/white_knight.png"));
        pieceTextures.put("WHITE_BISHOP", new Texture("pieces/white/white_bishop.png"));
        pieceTextures.put("WHITE_QUEEN", new Texture("pieces/white/white_queen.png"));
        pieceTextures.put("WHITE_KING", new Texture("pieces/white/white_king.png"));

        // Load black pieces
        pieceTextures.put("BLACK_PAWN", new Texture("pieces/black/black_pawn.png"));
        pieceTextures.put("BLACK_ROOK", new Texture("pieces/black/black_rook.png"));
        pieceTextures.put("BLACK_KNIGHT", new Texture("pieces/black/black_knight.png"));
        pieceTextures.put("BLACK_BISHOP", new Texture("pieces/black/black_bishop.png"));
        pieceTextures.put("BLACK_QUEEN", new Texture("pieces/black/black_queen.png"));
        pieceTextures.put("BLACK_KING", new Texture("pieces/black/black_king.png"));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        highlightTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private Texture getHighlightTexture() {
        return highlightTexture;
    }

    public static Texture getPieceTexture(Piece piece) {
        String key = piece.getColor().name() + "_" + piece.getClass().getSimpleName().toUpperCase();
        return pieceTextures.get(key);
    }

    @Override
    public void render() {
        handleInput();

        ScreenUtils.clear(0, 0, 0, 0);
        batch.begin();

        // Draw board
        int boardX = Gdx.graphics.getWidth() / 2 - boardSize / 2;
        int boardY = Gdx.graphics.getHeight() / 2 - boardSize / 2;
        batch.draw(boardTexture, boardX, boardY, boardSize, boardSize);

        // Calculer la taille exacte d'une case
        float squareSize = (float) boardSize / 8;

        if (game.isInCheck(PieceColor.WHITE)) {
            Position whiteKingPos = game.getWhiteKingPosition(); // Il faut ajouter cette méthode dans Game
            float x = boardX + (whiteKingPos.getColumn() * squareSize);
            float y = boardY + ((7 - whiteKingPos.getRow()) * squareSize);
            batch.setColor(1, 0, 0, 0.5f); // Rouge semi-transparent
            batch.draw(getHighlightTexture(), x, y, squareSize, squareSize);
            batch.setColor(1, 1, 1, 1); // Réinitialiser la couleur
        }

        if (game.isInCheck(PieceColor.BLACK)) {
            Position blackKingPos = game.getBlackKingPosition(); // Il faut ajouter cette méthode dans Game
            float x = boardX + (blackKingPos.getColumn() * squareSize);
            float y = boardY + ((7 - blackKingPos.getRow()) * squareSize);
            batch.setColor(1, 0, 0, 0.5f);
            batch.draw(getHighlightTexture(), x, y, squareSize, squareSize);
            batch.setColor(1, 1, 1, 1);
        }

        // Draw pieces
        Piece[][] pieces = game.getBoard().getBoard();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = pieces[row][col];
                if (piece != null) {
                    Texture pieceTexture = getPieceTexture(piece);
                    if (pieceTexture != null) {
                        // Calculer la position exacte de chaque pièce
                        float x = boardX + (col * squareSize);
                        // Pour l'axe Y, on inverse les coordonnées car l'origine est en bas à gauche dans LibGDX
                        float y = boardY + ((7 - row) * squareSize);

                        // Optionnel : ajuster la taille des pièces pour qu'elles soient légèrement plus petites que les cases
                        float pieceSize = squareSize * 0.7f; // 90% de la taille de la case
                        float offsetX = (squareSize - pieceSize) / 2;
                        float offsetY = (squareSize - pieceSize) / 2;

                        batch.draw(pieceTexture,
                            x + offsetX,  // Centrer horizontalement
                            y + offsetY,  // Centrer verticalement
                            pieceSize,    // Largeur de la pièce
                            pieceSize);   // Hauteur de la pièce
                    }
                }
            }
        }


        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        boardTexture.dispose();
        for (Texture texture : pieceTextures.values()) {
            texture.dispose();
        }
    }

    private void handleInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            int boardX = Gdx.graphics.getWidth() / 2 - boardSize / 2;
            int boardY = Gdx.graphics.getHeight() / 2 - boardSize / 2;
            float squareSize = boardSize / 8f;

            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (mouseX >= boardX && mouseX < boardX + boardSize &&
                mouseY >= boardY && mouseY < boardY + boardSize) {

                int col = (int)((mouseX - boardX) / squareSize);
                int row = 7 - (int)((mouseY - boardY) / squareSize);

                Position clickedPos = new Position(row, col);
                Piece[][] board = game.getBoard().getBoard();

                if (!isPieceSelected) {
                    // Premier clic - sélection de pièce
                    Piece clickedPiece = board[row][col];
                    if (clickedPiece != null &&
                        ((game.isWhiteTurn() && clickedPiece.getColor() == PieceColor.WHITE) ||
                            (!game.isWhiteTurn() && clickedPiece.getColor() == PieceColor.BLACK))) {
                        selectedPosition = clickedPos;
                        isPieceSelected = true;
                    }
                } else {
                    if (selectedPosition != null) {
                        if (game.movePiece(selectedPosition, clickedPos)) {
                            System.out.println("Pièce déplacée avec succès!");
                        } else {
                            System.out.println("Mouvement invalide!");
                        }
                        selectedPosition = null;
                        isPieceSelected = false;
                    }
                }
            }
        }

        // Clic droit pour désélectionner
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            selectedPosition = null;
            isPieceSelected = false;
        }
    }
}
