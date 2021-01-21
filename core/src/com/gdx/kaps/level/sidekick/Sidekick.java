package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.level.Gauge;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.renderer.AnimatedSprite;
import com.gdx.kaps.renderer.Renderable;

public class Sidekick implements Renderable {
    private final AnimatedSprite sprite;
    private final SidekickRecord type;
    private final Gauge gauge;

    public Sidekick(SidekickRecord record) {
        sprite = new AnimatedSprite("android/assets/img/sidekicks/" + record.path() + "_", 4, 250_000_000);
        type = record;
        gauge = new Gauge(record.maxGauge());
    }

    public Color color() {
        return type.color();
    }

    public Gauge gauge() {
        return gauge;
    }

    // update

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
