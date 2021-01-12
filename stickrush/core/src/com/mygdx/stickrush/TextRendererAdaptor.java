package com.mygdx.stickrush;

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.mygdx.stickrush.renderer.Shape;

import java.util.ArrayList;

import static com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import static java.util.Objects.requireNonNull;

public class TextRendererAdaptor {

    public static class TextBox {
        private final boolean centered;
        private final String text;
        private final Rectangle box;
        private final Color color;
        private final int size;

        public TextBox(String text, Rectangle box, Color color, int size, boolean centered) {
            this.centered = centered;
            this.text = text;
            this.box = box;
            this.color = color;
            this.size = size;
        }

        public static class TextBoxBuilder {
            private Color color = DEFAULT_COLOR;
            private int size = DEFAULT_SIZE;
            private final Rectangle box;
            private final String text;
            private boolean centered;

            public TextBoxBuilder(String text, Rectangle box) {
                this.text = text;
                this.box = box;
            }
            public TextBoxBuilder(String text, float x, float y, float width, float height) {
                this(text, new Rectangle(x, y, width, height));
            }

            public TextBoxBuilder setColor(Color color) {
                this.color = color;
                return this;
            }

            public TextBoxBuilder setColor(float r, float g, float b, float a) {
                return setColor(new Color(r, g, b, a));
            }

            public TextBoxBuilder setSize(int size) {
                this.size = size;
                return this;
            }

            public TextBoxBuilder center() {
                centered = true;
                return this;
            }

            public TextBox build() {
                if (size < 0) {
                    throw new IllegalArgumentException("Text size can't be negative. (" + size + ")");
                }
                return new TextBox(
                  requireNonNull(text),
                  requireNonNull(box),
                  requireNonNull(color),
                  size,
                  centered
                );
            }
        }
    }
    private static final FileHandle DEFAULT_FONT = Gdx.files.internal("fonts/Gotham.ttf");
    private static final Color DEFAULT_COLOR = new Color(1, 1, 1, 1);
    private static final int DEFAULT_SIZE = 25;
    private final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(DEFAULT_FONT);
    private final FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    private final ArrayList<TextBox> textBoxes = new ArrayList<>();
    private final SpriteBatch batch = new SpriteBatch();
    private BitmapFont font;

    public TextRendererAdaptor() {
        font = generator.generateFont(parameter);
    }

    public void add(TextBox textBox) {
        textBoxes.add(textBox);
    }

    private void drawText(String txt, float x, float y) {
        batch.setProjectionMatrix(Game.camera.combined);
        batch.begin();
        font.draw(batch, txt, x, y);
        batch.end();
    }

    private void drawCenteredText(String txt, float x, float y, float width, float height) {
        final GlyphLayout layout = new GlyphLayout(font, txt);
        // or for non final texts: layout.setText(font, text);

        final float fontX = x + (width - layout.width) / 2;
        final float fontY = y + (height + layout.height) / 2;

        batch.setProjectionMatrix(Game.camera.combined);
        batch.begin();
        font.draw(batch, layout, fontX, fontY);
        batch.end();
    }

    public void draw(TextBox text) {
        parameter.color = text.color;
        parameter.size = text.size;
        font = generator.generateFont(parameter);
        if (text.centered) {
            drawCenteredText(text.text, text.box.x, text.box.y, text.box.width, text.box.height);
        } else {
            drawText(text.text, text.box.x, text.box.y);
        }
    }

    public void drawAll() {
        for (TextBox textbox : textBoxes) {
            draw(textbox);
        }
    }
}






