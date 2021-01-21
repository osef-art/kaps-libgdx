package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.level.Gauge;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.renderer.AnimatedSprite;
import com.gdx.kaps.renderer.Renderable;

import java.util.*;
import java.util.stream.Collectors;

public class Sidekick implements Renderable {
    private final AnimatedSprite sprite;
    private final SidekickRecord type;
    private final boolean hasCooldown;
    private final Gauge gauge;

    public Sidekick(SidekickRecord record) {
        sprite = new AnimatedSprite("android/assets/img/sidekicks/" + record.path() + "_", 4, 200_000_000);
        hasCooldown = record.maxMana() < record.cooldown();
        gauge = hasCooldown ?
                  new Gauge(record.cooldown(), record.cooldown()) :
                  new Gauge(record.maxMana());
        type = record;
    }

    public static Set<Sidekick> randomSetOf(int n) {
        var sdks = Arrays.stream(SidekickRecord.values())
          .map(Sidekick::new)
          .collect(Collectors.toList());
        var set = new HashSet<Sidekick>();
        for (int i = 0; i < n; i++) {
            set.add(sdks.remove(new Random().nextInt(sdks.size())));
        }
        return set;
    }

    public static Set<Sidekick> setOf(SidekickRecord ... sidekicks) {
        return Arrays.stream(sidekicks)
                 .map(Sidekick::new)
                 .collect(Collectors.toSet());
    }

    public Color color() {
        return type.color();
    }

    public Gauge gauge() {
        return gauge;
    }

    public void increaseMana() {
        if (!hasCooldown) gauge.increase();
    }

    public void decreaseCooldown() {
        if (hasCooldown) gauge.decrease();
    }

    public void trigger(Level level) {
        type.power().accept(level);
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

    public boolean isReady() {
        return (hasCooldown && gauge.isEmpty()) || (!hasCooldown && gauge.isFull());
    }

    public void reset() {
        if (hasCooldown) gauge.fill();
        else gauge.empty();
    }
}
