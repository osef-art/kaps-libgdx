package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.Sound;
import com.gdx.kaps.level.Gauge;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.renderer.AnimatedSprite;
import com.gdx.kaps.renderer.Renderable;
import com.gdx.kaps.renderer.Zone;

import java.util.*;
import java.util.stream.Collectors;

import static com.gdx.kaps.MainScreen.*;

public class Sidekick implements Renderable {
    private final AnimatedSprite sprite;
    private final SidekickRecord type;
    private final boolean hasCooldown;
    private final Gauge gauge;

    public Sidekick(SidekickRecord record) {
        // TODO: sub-class with cooldown + Factory
        sprite = new AnimatedSprite(
          "android/assets/img/sidekicks/" + record.path() + "_",
          4, 200_000_000
        );
        hasCooldown = record.maxMana() < record.cooldown();
        gauge = hasCooldown ?
                  new Gauge(record.cooldown(), record.cooldown()) :
                  new Gauge(record.maxMana());
        type = record;
    }

    public static Set<Sidekick> randomSetOf(int n, Set<Sidekick> include) {
        var sdks = Arrays.stream(SidekickRecord.values())
          .map(Sidekick::new)
          .collect(Collectors.toList());

        while (include.size() < n) {
            include.add(sdks.remove(new Random().nextInt(sdks.size())));
        }
        return include;
    }

    public Color color() {
        return type.color();
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

    public void triggerIfReady(Level lvl) {
        if (isReady()) {
            Sound.play("trigger" + (hasCooldown ? "_cooldown" : ""));
            trigger(lvl);
            reset();
        }
    }

    public void render(int n) {
        // fond
        sra.drawRect(
          dim.get(Zone.SIDE_PANEL).x,
          dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * n),
          dim.get(Zone.SIDE_PANEL).width,
          dim.sidekickPanelHeight,
          new com.badlogic.gdx.graphics.Color(0.45f, 0.5f, 0.6f, 1)
        );

        render(
          dim.get(Zone.SIDE_PANEL).x + 5,
          dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * n) + 5,
          dim.sidekickPanelHeight - 10,
          dim.sidekickPanelHeight - 10
        );

        // gauge
        if (hasCooldown) {
            gauge.renderCircled(
              dim.get(Zone.SIDE_PANEL).x + dim.sidekickPanelHeight + (dim.sidekickPanelHeight - 10)/2,
              dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height  * (2 + 0.5f * n) + dim.sidekickPanelHeight - 30,
              dim.sidekickPanelHeight / 2 - 10,
              5,
              new com.badlogic.gdx.graphics.Color(0.45f, 0.5f, 0.6f, 1),
              color().value()
            );

            tra.drawText(
              gauge.value() + "",
              dim.get(Zone.SIDE_PANEL).x + dim.sidekickPanelHeight,
              dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * n) + 5,
              dim.sidekickPanelHeight - 10,
              dim.sidekickPanelHeight / 2 - 10
            );
        } else {
            gauge.render(
              dim.get(Zone.SIDE_PANEL).x + dim.sidekickPanelHeight,
              dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height  * (2 + 0.5f * n) + dim.sidekickPanelHeight - 30,
              dim.get(Zone.SIDE_PANEL).width - (dim.sidekickPanelHeight - 10) - 15,
              20,
              new com.badlogic.gdx.graphics.Color(0.5f, 0.55f, 0.65f, 1),
              color().value()
            );

            tra.drawText(
              gauge.value() + " / " + gauge.max(),
              dim.get(Zone.SIDE_PANEL).x + 10 + dim.sidekickPanelHeight - 10,
              dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * n) + 10
            );
        }
    }
}
