package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.MainScreen;
import com.gdx.kaps.SoundStream;
import com.gdx.kaps.level.Gauge;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.renderer.Animated;
import com.gdx.kaps.renderer.AnimatedSprite;
import com.gdx.kaps.renderer.NonStatic;
import com.gdx.kaps.renderer.Zone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gdx.kaps.MainScreen.dim;
import static com.gdx.kaps.MainScreen.tra25;

public class Sidekick implements Animated, NonStatic {
    private final SoundStream attacks = new SoundStream();
    private final AnimatedSprite sprite;
    final SidekickRecord type;
    final Gauge gauge;

    // init

    Sidekick(SidekickRecord record) {
        sprite = new AnimatedSprite(
          "android/assets/img/sidekicks/" + record + "_",
          4, 200_000_000
        );
        this.gauge = new Gauge(record.maxMana());
        type = record;
    }

    private static Sidekick of(SidekickRecord record) {
        return record.maxMana() < record.cooldown() ?
                 new SidekickCooldown(record) :
                 new Sidekick(record);
    }

    public static Set<Sidekick> randomSetOf(int n, Set<SidekickRecord> include) {
        var sdks = new ArrayList<>(Arrays.asList(SidekickRecord.values()));

        while (include.size() < n) {
            include.add(sdks.remove(new Random().nextInt(sdks.size())));
        }
        return include.stream()
                 .map(Sidekick::of)
                 .collect(Collectors.toUnmodifiableSet());
    }

    // getters

    public Color color() {
        return type.color();
    }

    // predicates

    boolean isReady() {
        return gauge.isFull();
    }

    public boolean hasCooldown() {
        return false;
    }

    // actions

    public void increaseMana() {
        gauge.increase();
    }

    public void decreaseCooldown() {
    }

    void reset() {
        gauge.empty();
    }

    void trigger(Level level) {
        MainScreen.shake();
        playSound(type.sound());
        type.power().accept(level, type);
    }

    public void triggerIfReady(Level lvl) {
        if (isReady()) {
            trigger(lvl);
            reset();
            lvl.updateGrid();
        }
    }

    public void playSound(String path) {
        attacks.play(path);
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

    public void renderGauge(int n) {
        gauge.render(
          dim.get(Zone.SIDE_PANEL).x + dim.sidekickPanelHeight,
          dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * n) + dim.sidekickPanelHeight - 30,
          dim.get(Zone.SIDE_PANEL).width - (dim.sidekickPanelHeight - 10) - 15,
          20,
          new com.badlogic.gdx.graphics.Color(0.5f, 0.55f, 0.65f, 1),
          color().value()
        );

        tra25.drawText(
          gauge.value() + " / " + gauge.max(),
          dim.get(Zone.SIDE_PANEL).x + 10 + dim.sidekickPanelHeight - 10,
          dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * n) + 10
        );
    }
}
