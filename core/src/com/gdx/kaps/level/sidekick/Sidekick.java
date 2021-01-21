package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.level.Gauge;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.renderer.AnimatedSprite;
import com.gdx.kaps.renderer.Renderable;

import java.util.*;
import java.util.stream.Collectors;

public class Sidekick implements Renderable {
    private final AnimatedSprite sprite;
    private final SidekickRecord type;
    private final Gauge gauge;

    public Sidekick(SidekickRecord record) {
        sprite = new AnimatedSprite("android/assets/img/sidekicks/" + record.path() + "_", 4, 250_000_000);
        type = record;
        gauge = new Gauge(record.maxGauge());
    }

    public static Set<Sidekick> RandomSetOf(int n) {
        var sdks = Arrays.stream(SidekickRecord.values())
          .map(Sidekick::new)
          .collect(Collectors.toList());
        while (sdks.size() > n) sdks.remove(new Random().nextInt(sdks.size()));
        return new HashSet<>(sdks);
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
