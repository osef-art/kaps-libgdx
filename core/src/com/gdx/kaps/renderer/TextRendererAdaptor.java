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
    private final BitmapFont shade;
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
        shade = generator.generateFont(parameter);
        shade.setColor(0, 0, 0, 0.25f);
        generator.dispose();
    }

    public void drawText(String txt, float x, float y) {
        spra.renderText(txt, font, x, y);
    }

    public void drawText(String txt, float x, float y, float width, float height) {
        spra.renderText(txt, font, x, y, width, height - fontSize * 1.25f);
    }

    public void drawShadedText(String txt, float x, float y) {
        spra.renderText(txt, shade, x, y + fontSize * 0.2f);
        drawText(txt, x, y);
    }

    public void drawShadedText(String txt, float x, float y, float width, float height) {
        spra.renderText(txt, shade, x, y + fontSize * 0.2f, width, height - fontSize * 1.25f);
        drawText(txt, x, y, width, height);
    }

    public void drawShadedText(String txt, Rectangle rect) {
        drawShadedText(txt, rect.x, rect.y, rect.width, rect.height);
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

    public void formatSidekicksDesc(float width) {
        stream(SidekickRecord.values()).forEach(sdk -> sdk.setUsageLines(stringBoxedIn(sdk.usage(), width)));
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
