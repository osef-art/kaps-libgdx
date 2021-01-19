package com.gdx.kaps.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import static com.gdx.kaps.MainScreen.*;

public class TextRendererAdaptor {
    private final FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    private final BitmapFont font;

    public TextRendererAdaptor() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("android/assets/fonts/Gotham.ttf"));
        parameter.color = new Color(1, 1, 1, 1);
        parameter.flip = true;
        parameter.size = 25;
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    private static Color toGDXColor(java.awt.Color color) {
        return new Color(
          (float) (color.getRed() / 255.),
          (float) (color.getGreen() / 255.),
          (float) (color.getBlue() / 255.),
          (float) (color.getAlpha() / 255.)
        );
    }

    public void drawText(float value, float x, float y) {
        drawText(value + "", x, y);
    }

    public void drawText(String text, float x, float y) {
        batch.begin();
        font.draw(batch, text, x, y);
        batch.end();
    }
}
