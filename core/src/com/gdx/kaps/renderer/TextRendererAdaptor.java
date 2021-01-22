package com.gdx.kaps.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Rectangle;

import static com.gdx.kaps.MainScreen.spra;

public class TextRendererAdaptor implements RendererAdaptor {
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
        spra.renderText(text, font, x, y);
    }


    public void drawText(String txt, Rectangle rect) {
        drawText(txt, rect.x, rect.y, rect.width, rect.height);
    }

    public void drawText(String txt, float x, float y, float width, float height) {
        spra.renderText(txt, font, x, y, width, height);
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
