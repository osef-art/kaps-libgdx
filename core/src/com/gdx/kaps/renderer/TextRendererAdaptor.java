package com.gdx.kaps.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Rectangle;

import static com.gdx.kaps.MainScreen.*;

public class TextRendererAdaptor {
    private final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("android/assets/fonts/Gotham.ttf"));
    private final FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    private BitmapFont font;

    public TextRendererAdaptor() {
        parameter.color = new Color(1, 1, 1, 1);
        parameter.flip = true;
        parameter.size = 25;
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    public void drawText(float value, float x, float y) {
        drawText(value + "", x, y);
    }

    public void drawText(String text, float x, float y) {
        batch.begin();
        font.draw(batch, text, x, y);
        batch.end();
    }

    public void drawCenteredText(String txt, Rectangle rect) {
        drawCenteredText(txt, rect.x, rect.y, rect.width, rect.height);
    }

    public void drawCenteredText(String txt, float x, float y, float width, float height) {
        final GlyphLayout layout = new GlyphLayout(font, txt);
        // or for non final texts: layout.setText(font, text);

        final float fontX = x + (width - layout.width) / 2;
        final float fontY = y + (height + layout.height) / 2;

        batch.begin();
        font.draw(batch, layout, fontX, fontY);
        batch.end();
    }

    public void setFontSizeAndColor(int size, Color color) {
        // IMPL: to be changed. Builder ?
        parameter.color = color;
        parameter.size = size;
        font = generator.generateFont(parameter);
    }
}
