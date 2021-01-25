package com.gdx.kaps.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

import static com.gdx.kaps.MainScreen.camera;

public class ShapeRendererAdaptor implements RendererAdaptor {
    private final ShapeRenderer rd = new ShapeRenderer();

    private void draw(Runnable action, Color c) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        rd.setProjectionMatrix(camera.combined);
        rd.begin(ShapeType.Filled);
        rd.setColor(c.r, c.g, c.b, c.a);
        action.run();
        rd.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void drawCircle(float x, float y, float radius, Color c) {
        draw(() -> rd.circle(x, y, radius), c);
    }

    public void drawArc(float x, float y, float radius, float start, float degrees, Color c) {
        draw(() -> rd.arc(x, y, radius, start, degrees), c);
    }

    public void drawRect(float x, float y, float w, float h, Color c) {
        draw(() -> rd.rect(x, y, w, h), c);
    }

    public void drawRect(Rectangle r, Color c) {
        drawRect(r.x, r.y, r.width, r.height, c);
    }

    @Override
    public void dispose() {
        rd.dispose();
    }
}
