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
    // IMPL: ability to change size/color and handle memory ?
    //  or one renderer per color/size ?
    private final BitmapFont font;

    public TextRendererAdaptor(int size, Color color) {
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.color = color;
        parameter.flip = true;
        parameter.size = size;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("android/assets/fonts/Gotham.ttf"));
        font = generator.generateFont(parameter);
        generator.dispose();
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
}
