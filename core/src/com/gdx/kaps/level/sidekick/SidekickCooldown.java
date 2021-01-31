package com.gdx.kaps.level.sidekick;

import com.badlogic.gdx.graphics.Color;
import com.gdx.kaps.level.Gauge;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.renderer.Zone;

import static com.gdx.kaps.MainScreen.dim;
import static com.gdx.kaps.MainScreen.tra25;

class SidekickCooldown extends Sidekick {
    private final Gauge cooldown;

    SidekickCooldown(SidekickRecord record) {
        super(record);
        cooldown = new Gauge(record.cooldown(), record.cooldown());
    }

    @Override
    public boolean isReady() {
        return cooldown.isEmpty();
    }

    public boolean hasCooldown() {
        return true;
    }

    @Override
    public void increaseMana() {
    }

    @Override
    public void decreaseCooldown() {
        cooldown.decrease();
    }


    @Override
    public void reset() {
        cooldown.fill();
    }

    void trigger(Level level) {
        playSound(type.sound());
        type.power().accept(level, type);
    }

    public void triggerIfReady(Level lvl) {
        if (isReady()) {
            trigger(lvl);
            reset();
        }
    }

    public void renderGauge(int n) {
        cooldown.renderCircled(
          dim.get(Zone.SIDE_PANEL).x + dim.sidekickPanelHeight + (dim.sidekickPanelHeight - 10)/2,
          dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height  * (2 + 0.5f * n) + dim.sidekickPanelHeight - 30,
          dim.sidekickPanelHeight / 2 - 10,
          5,
          new Color(0.35f, 0.4f, 0.5f, 1),
          color().value()
        );

        cooldown.renderBoxed(
          dim.get(Zone.SIDE_PANEL).x,
          dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * n),
          dim.get(Zone.SIDE_PANEL).width,
          dim.sidekickPanelHeight,
          new Color(0,0,0,0),
          type.color().value(0.45f),
          true
        );

        tra25.drawText(
          cooldown.value() + "",
          dim.get(Zone.SIDE_PANEL).x + dim.sidekickPanelHeight + 5,
          dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * n) + 10,
          dim.sidekickPanelHeight - 20,
          dim.sidekickPanelHeight - 20
        );
    }
}
