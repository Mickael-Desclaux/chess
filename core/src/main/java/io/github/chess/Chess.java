package io.github.chess;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.chess.entities.*;
import io.github.chess.enums.PieceColor;

import java.util.HashMap;
import java.util.Map;

public class Chess extends ApplicationAdapter {

    private static Map<String, Texture> pieceTextures;
    private Texture boardTexture;
    private SpriteBatch batch;
    private final int boardSize = 480;
    private Position selectedPosition;
    private boolean isPieceSelected;
    private Game game;
    private Texture highlightTexture;
    private boolean gameOver = false;
    private PieceColor winner = null;
    private Texture gameOverBackground;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 50;
    private Position selectedKingPosition;


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

        Pixmap highlightPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        highlightPixmap.setColor(1, 1, 1, 1);
        highlightPixmap.fill();
        highlightTexture = new Texture(highlightPixmap);
        highlightPixmap.dispose();
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0, 0.7f);
        bgPixmap.fill();
        gameOverBackground = new Texture(bgPixmap);
        bgPixmap.dispose();
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
        try {
            if (!gameOver) {
                boolean whiteCheckmated = game.isCheckmate(PieceColor.WHITE);
                boolean blackCheckmated = game.isCheckmate(PieceColor.BLACK);

                if (whiteCheckmated || blackCheckmated) {
                    gameOver = true;
                    winner = whiteCheckmated ? PieceColor.BLACK : PieceColor.WHITE;
                }
            }
        } catch (Exception e) {
            Gdx.app.error("Chess", "Error in render()", e);
        }

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
            Position blackKingPos = game.getBlackKingPosition();
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

        // Afficher les options de promotion si nécessaire
        if (game.isWaitingForPromotionSelection()) {
            int buttonSize = 64;
            int buttonSpacing = 10;
            int startX = Gdx.graphics.getWidth() / 2 - (2 * buttonSize + buttonSpacing);
            int startY = Gdx.graphics.getHeight() / 2 - buttonSize / 2;

            // Dessiner les boutons de promotion
            batch.draw(getPieceTexture(new Queen(game.getPromotionColor(), game.getPromotionPosition(), game)), startX, startY, buttonSize, buttonSize);
            batch.draw(getPieceTexture(new Rook(game.getPromotionColor(), game.getPromotionPosition(), game)), startX + buttonSize + buttonSpacing, startY, buttonSize, buttonSize);
            batch.draw(getPieceTexture(new Bishop(game.getPromotionColor(), game.getPromotionPosition(), game)), startX, startY - buttonSize - buttonSpacing, buttonSize, buttonSize);
            batch.draw(getPieceTexture(new Knight(game.getPromotionColor(), game.getPromotionPosition(), game)), startX + buttonSize + buttonSpacing, startY - buttonSize - buttonSpacing, buttonSize, buttonSize);
        }

        if (gameOver) {
            renderGameOverScreen();
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
        if (highlightTexture != null) {
            highlightTexture.dispose();
        }
        gameOverBackground.dispose();
    }

    private void handleInput() {
        if (!gameOver) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                int boardX = Gdx.graphics.getWidth() / 2 - boardSize / 2;
                int boardY = Gdx.graphics.getHeight() / 2 - boardSize / 2;
                float squareSize = boardSize / 8f;

                if (mouseX >= boardX && mouseX < boardX + boardSize &&
                    mouseY >= boardY && mouseY < boardY + boardSize) {

                    int col = (int) ((mouseX - boardX) / squareSize);
                    int row = 7 - (int) ((mouseY - boardY) / squareSize);

                    Position clickedPos = new Position(row, col);
                    Piece[][] board = game.getBoard().getBoard();

                    if (!isPieceSelected) {
                        // Premier clic - sélection de pièce
                        Piece clickedPiece = board[row][col];
                        if (clickedPiece != null &&
                            ((game.isWhiteTurn() && clickedPiece.getColor() == PieceColor.WHITE) ||
                                (!game.isWhiteTurn() && clickedPiece.getColor() == PieceColor.BLACK))) {
                            selectedPosition = clickedPos;
                            if (clickedPiece instanceof King) {
                                selectedKingPosition = clickedPos;
                            }
                            isPieceSelected = true;
                        }
                    } else {
                        if (selectedKingPosition != null) {
                            // Vérifier si le deuxième clic est sur une tour pour le roque
                            Piece clickedPiece = board[row][col];
                            if (clickedPiece instanceof Rook && clickedPiece.getColor() == game.getBoard().getBoard()[selectedKingPosition.getRow()][selectedKingPosition.getColumn()].getColor()) {
                                boolean isKingSide = col > selectedKingPosition.getColumn();
                                boolean moveSuccessful = game.castle(clickedPiece.getColor(), isKingSide);
                                if (!moveSuccessful) {
                                    System.out.println("Roque invalide!");
                                }
                            } else {
                                boolean moveSuccessful = game.movePiece(selectedPosition, clickedPos);
                                if (!moveSuccessful && !game.isWaitingForPromotionSelection()) {
                                    System.out.println("Mouvement invalide!");
                                }
                            }
                            selectedKingPosition = null;
                        } else {
                            boolean moveSuccessful = game.movePiece(selectedPosition, clickedPos);
                            if (!moveSuccessful && !game.isWaitingForPromotionSelection()) {
                                System.out.println("Mouvement invalide!");
                            }
                        }
                        selectedPosition = null;
                        isPieceSelected = false;
                    }
                }
            }

            // Clic droit pour désélectionner
            if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                selectedPosition = null;
                selectedKingPosition = null;
                isPieceSelected = false;
            }

            // Gérer la sélection de promotion
            if (game.isWaitingForPromotionSelection() && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                handlePromotionSelection(mouseX, mouseY);
            }
        } else {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                float mouseX = Gdx.input.getX();
                float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
                int screenHeight = Gdx.graphics.getHeight();
                int screenWidth = Gdx.graphics.getWidth();

                // Zone du bouton "New Game"
                Rectangle newGameButton = new Rectangle(
                    screenWidth / 2 - BUTTON_WIDTH / 2,
                    screenHeight / 2 - BUTTON_HEIGHT / 2,
                    BUTTON_WIDTH,
                    BUTTON_HEIGHT
                );

                // Zone du bouton "Quit"
                Rectangle quitButton = new Rectangle(
                    screenWidth / 2 - BUTTON_WIDTH / 2,
                    screenHeight / 2 - BUTTON_HEIGHT * 2,
                    BUTTON_WIDTH,
                    BUTTON_HEIGHT
                );

                if (newGameButton.contains(mouseX, mouseY)) {
                    restartGame();
                } else if (quitButton.contains(mouseX, mouseY)) {
                    Gdx.app.exit();
                }
            }
        }
    }

    private void handlePromotionSelection(float mouseX, float mouseY) {
        // Définir les positions des boutons de promotion
        int buttonSize = 64;
        int buttonSpacing = 10;
        int startX = Gdx.graphics.getWidth() / 2 - (2 * buttonSize + buttonSpacing);
        int startY = Gdx.graphics.getHeight() / 2 - buttonSize / 2;

        // Vérifier quel bouton a été cliqué
        if (mouseX >= startX && mouseX < startX + buttonSize &&
            mouseY >= startY && mouseY < startY + buttonSize) {
            promotePawn(game.getPromotionPosition(), game.getPromotionColor(), Queen.class);
        } else if (mouseX >= startX + buttonSize + buttonSpacing && mouseX < startX + 2 * buttonSize + buttonSpacing &&
            mouseY >= startY && mouseY < startY + buttonSize) {
            promotePawn(game.getPromotionPosition(), game.getPromotionColor(), Rook.class);
        } else if (mouseX >= startX && mouseX < startX + buttonSize &&
            mouseY >= startY - buttonSize - buttonSpacing && mouseY < startY - buttonSpacing) {
            promotePawn(game.getPromotionPosition(), game.getPromotionColor(), Bishop.class);
        } else if (mouseX >= startX + buttonSize + buttonSpacing && mouseX < startX + 2 * buttonSize + buttonSpacing &&
            mouseY >= startY - buttonSize - buttonSpacing && mouseY < startY - buttonSpacing) {
            promotePawn(game.getPromotionPosition(), game.getPromotionColor(), Knight.class);
        }
    }

    private void promotePawn(Position position, PieceColor color, Class<? extends Piece> pieceClass) {
        try {
            Piece promotedPiece = pieceClass.getConstructor(PieceColor.class, Position.class, Game.class)
                .newInstance(color, position, game);
            game.getBoard().getBoard()[position.getRow()][position.getColumn()] = promotedPiece;
            System.out.println("Pawn promoted to " + promotedPiece.getClass().getSimpleName());
            game.setPromoting(false);
            game.setWaitingForPromotionSelection(false);
            game.setWhiteTurn(!game.isWhiteTurn()); // Changer le tour après la promotion
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while promoting the pawn: " + e.getMessage());
        }
    }

    private void renderGameOverScreen() {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        // Fond semi-transparent
        batch.setColor(1, 1, 1, 0.7f);
        batch.draw(gameOverBackground, 0, 0, screenWidth, screenHeight);
        batch.setColor(1, 1, 1, 1);

        // Dessiner le texte du gagnant
        String winnerText = (winner == PieceColor.WHITE ? "White" : "Black") + " wins!";
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2);
        GlyphLayout layout = new GlyphLayout(font, winnerText);
        float textX = (screenWidth - layout.width) / 2;
        float textY = screenHeight / 2 + 100;
        font.setColor(1, 1, 1, 1);
        font.draw(batch, winnerText, textX, textY);

        // Dessiner les boutons
        drawButton("New Game", screenWidth / 2 - BUTTON_WIDTH / 2,
            screenHeight / 2 - BUTTON_HEIGHT / 2);
        drawButton("Quit", screenWidth / 2 - BUTTON_WIDTH / 2,
            screenHeight / 2 - BUTTON_HEIGHT * 2);
    }

    private void drawButton(String text, float x, float y) {
        // Fond du bouton
        batch.setColor(0.3f, 0.3f, 0.3f, 1);
        batch.draw(highlightTexture, x, y, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Texte du bouton
        BitmapFont font = new BitmapFont();
        GlyphLayout layout = new GlyphLayout(font, text);
        float textX = x + (BUTTON_WIDTH - layout.width) / 2;
        float textY = y + BUTTON_HEIGHT / 2 + layout.height / 2;
        font.setColor(1, 1, 1, 1);
        font.draw(batch, text, textX, textY);
    }

    private void restartGame() {
        game = new Game();
        gameOver = false;
        winner = null;
        selectedPosition = null;
        isPieceSelected = false;

        // Forcer un nettoyage du batch
        if (batch != null) {
            batch.setColor(1, 1, 1, 1);
        }
    }
}
