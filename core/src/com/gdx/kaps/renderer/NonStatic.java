package com.gdx.kaps.renderer;

import com.badlogic.gdx.math.Rectangle;

public interface NonStatic extends Renderable {
    void render(float x, float y, float width, float height);

    default void render(Rectangle zone) {
        render(zone.x, zone.y, zone.width, zone.height);
    }
}
