package io.github.chess;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Chess extends ApplicationAdapter {

    Texture boardTexture;
    SpriteBatch batch;
    int boardSize = 468;

    @Override
    public void create() {
        boardTexture = new Texture("board/board.png");
        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 0);
        batch.begin();
        int x = Gdx.graphics.getWidth() / 2 - boardSize / 2;
        int y = Gdx.graphics.getHeight() / 2 - boardSize / 2;
        batch.draw(boardTexture, x, y, boardSize, boardSize);
        batch.end();
    }
}
