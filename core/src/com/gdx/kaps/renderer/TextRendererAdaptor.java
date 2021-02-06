package com.gdx.kaps.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Rectangle;
import com.gdx.kaps.level.sidekick.SidekickRecord;

import java.util.ArrayList;
import java.util.Objects;

import static com.gdx.kaps.MainScreen.spra;
import static java.util.Arrays.*;

public class TextRendererAdaptor implements RendererAdaptor {
    private final BitmapFont font;
    private final float fontSize;

    public TextRendererAdaptor(int size, Color color) {
        fontSize = size;
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
        spra.renderText(txt, font, x, y, width, height - fontSize * 1.25f);
    }

    private String[] stringBoxedIn(String str, float width) {
        Objects.requireNonNull(str);
        int start = 0;
        String line = "";
        var words = str.split(" ");
        ArrayList<String> lines = new ArrayList<>();

        for (int i = 0; i < words.length; i++) {
            line = String.join(" ", copyOfRange(words, start, i + 1));
            var glyph = new GlyphLayout(font, line);

            if (glyph.width >= width) {
                lines.add(line);
                start = i;
                line = "";
            }
        }
        if (!line.equals("")) lines.add(line);
        return lines.toArray(new String[0]);
    }

    @Override
    public void dispose() {
        font.dispose();
    }

    public void formatSidekicksDesc(float width) {
        stream(SidekickRecord.values()).forEach(sdk -> sdk.setUsageLines(stringBoxedIn(sdk.usage(), width)));
    }
}
