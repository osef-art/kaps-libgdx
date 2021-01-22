package com.gdx.kaps.renderer;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static com.gdx.kaps.MainScreen.camera;

public class SpriteRendererAdaptor {
    private final SpriteBatch batch = new SpriteBatch();

    public SpriteRendererAdaptor() {
        batch.setProjectionMatrix(camera.combined);
    }

    public void dispose() {
        batch.dispose();
    }

    public void render(Sprite sprite) {
        sprite.draw(batch);
    }

    public void render(Sprite sprite, float x, float y, float width, float height) {
        batch.begin();
        batch.draw(sprite, x, y, width, height);
        batch.end();
    }

    public void render(Sprite sprite, float x, float y, float width, float height, float alpha) {
        batch.begin();
        batch.setColor(1,1,1, alpha);
        batch.draw(sprite, x, y, width, height);
        batch.end();
    }

    public void renderText(String text, BitmapFont font, float x, float y) {
        batch.begin();
        font.draw(batch, text, x, y);
        batch.end();
    }

    public void renderText(String txt, BitmapFont font, float x, float y, float width, float height) {
        final GlyphLayout layout = new GlyphLayout(font, txt);
        final float fontX = x + (width - layout.width) / 2;
        final float fontY = y + (height + layout.height) / 2;

        batch.begin();
        font.draw(batch, layout, fontX, fontY);
        batch.end();
    }
}
