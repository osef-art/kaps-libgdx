package com.mygdx.stickrush;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.stickrush.renderer.ShapeRendererAdaptor;

public abstract class ShapeDrawable implements DrawableInterface {
    final com.mygdx.stickrush.renderer.ShapeRendererAdaptor sra = new ShapeRendererAdaptor();

    @Override
    public Texture currentFrame() {
        return null;
    }

    @Override
    public void dispose() {
        sra.dispose();
    }

    @Override
    public void render() {

    }

    @Override
    public void addTextures() {

    }
}
