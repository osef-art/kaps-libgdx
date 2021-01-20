package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.renderer.AnimatedSprite;
import com.gdx.kaps.renderer.Renderable;

public class Sidekick implements Renderable {
    private final AnimatedSprite sprite;
    private final SidekickRecord type;

    public Sidekick(SidekickRecord record) {
        sprite = new AnimatedSprite("android/assets/img/sidekicks/" + record.path() + "_", 4, 250_000_000);
        type = record;
    }

    public Color color() {
        return type.color();
    }

    @Override
    public void update() {
        sprite.update();
    }

    @Override
    public void render() {
    }

    @Override
    public void render(float x, float y, float width, float height) {
        sprite.render(x, y, width, height);
    }
}
