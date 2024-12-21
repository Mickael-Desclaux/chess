package io.github.chess;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Chess extends ApplicationAdapter {

    Texture boardTexture;
    Texture whitePawnTexture;
    Texture blackPawnTexture;
    SpriteBatch batch;

    private OrthographicCamera camera;
    private Viewport viewport;
    private static final int BOARD_SIZE = 500;

    @Override
    public void create() {
        boardTexture = new Texture("board/board.png");
        whitePawnTexture = new Texture("pieces/white/white_pawn.png");
        blackPawnTexture = new Texture("pieces/black/black_pawn.png");
        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 0);
        batch.begin();
        int x = Gdx.graphics.getWidth() / 2 - BOARD_SIZE / 2;
        int y = Gdx.graphics.getHeight() / 2 - BOARD_SIZE / 2;
        batch.draw(boardTexture, x, y, BOARD_SIZE, BOARD_SIZE);
        batch.end();
    }
}
