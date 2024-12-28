package io.github.chess;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.chess.entities.Board;
import io.github.chess.entities.Piece;

import java.util.HashMap;
import java.util.Map;

public class Chess extends ApplicationAdapter {

    private static Map<String, Texture> pieceTextures;
    private Texture boardTexture;
    private SpriteBatch batch;
    private Board board;
    private int boardSize = 480;

    @Override
    public void create() {
        board = new Board();
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
    }

    public static Texture getPieceTexture(Piece piece) {
        String key = piece.getColor().name() + "_" + piece.getClass().getSimpleName().toUpperCase();
        return pieceTextures.get(key);
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 0);
        batch.begin();

        // Draw board
        int boardX = Gdx.graphics.getWidth() / 2 - boardSize / 2;
        int boardY = Gdx.graphics.getHeight() / 2 - boardSize / 2;
        batch.draw(boardTexture, boardX, boardY, boardSize, boardSize);

        // Calculer la taille exacte d'une case
        float squareSize = (float) boardSize / 8;

        // Draw pieces
        Piece[][] pieces = board.getBoard();
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
}
