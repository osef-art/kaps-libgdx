package com.gdx.kaps.renderer;

public interface Renderable {
    // INFO: temporary interface to render Level state
    void update();
    void render();
    void render(float x, float y, float width, float height);
}
