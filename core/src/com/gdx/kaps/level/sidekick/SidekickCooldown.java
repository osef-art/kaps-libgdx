package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.Sound;
import com.gdx.kaps.level.Gauge;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.renderer.Zone;

import static com.gdx.kaps.MainScreen.dim;
import static com.gdx.kaps.MainScreen.tra;

class SidekickCooldown extends Sidekick {
    SidekickCooldown(SidekickRecord record) {
        super(record, new Gauge(record.cooldown(), record.cooldown()));
    }

    @Override
    public void increaseMana() {
    }

    @Override
    public void decreaseCooldown() {
        gauge.decrease();
    }

    @Override
    public boolean isReady() {
        return gauge.isEmpty();
    }

    @Override
    public void reset() {
        gauge.fill();
    }

    void trigger(Level level) {
        Sound.play(type.sound());
        type.power().accept(level);
    }

    public void triggerIfReady(Level lvl) {
        if (isReady()) {
            trigger(lvl);
            reset();
        }
    }

    public void renderGauge(int n) {
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
    }
}
