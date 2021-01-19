package com.gdx.kaps.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

import static com.gdx.kaps.MainScreen.camera;

public class ShapeRendererAdaptor {
    private final ShapeRenderer rd = new ShapeRenderer();

    public void drawCircle(float x, float y, float radius) {
        rd.setProjectionMatrix(camera.combined);
        rd.begin(ShapeType.Line);
        rd.setColor(1, 1, 1, 1);
        rd.circle(x, y, radius);
        rd.end();
    }

    private void drawRect(float x, float y, float w, float h, Color c, ShapeType type) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        rd.setProjectionMatrix(camera.combined);
        rd.begin(type);
        rd.setColor(c.r, c.g, c.b, c.a);
        rd.rect(x, y, w, h);
        rd.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void drawRect(Rectangle r, Color c) {
        drawRect(r.x, r.y, r.width, r.height, c, ShapeType.Filled);
    }

    public void drawRect(float x, float y, float w, float h, Color c) {
        drawRect(x, y, w, h, c, ShapeType.Filled);
    }

    public void drawOutlineRect(Rectangle r, Color c) {
        drawRect(r.x, r.y, r.width, r.height, c, ShapeType.Line);
    }

    public void drawOutlineRect(float x, float y, float w, float h, Color c) {
        drawRect(x, y, w, h, c, ShapeType.Line);
    }

    public void dispose() {
        rd.dispose();
    }
}
