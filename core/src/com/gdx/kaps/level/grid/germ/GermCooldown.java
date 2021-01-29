package com.gdx.kaps.level.grid.germ;

import com.gdx.kaps.level.Gauge;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Color;

import static com.gdx.kaps.MainScreen.dim;
import static com.gdx.kaps.MainScreen.tra15;

public abstract class GermCooldown extends Germ {
    private final Gauge cooldown;

    GermCooldown(int x, int y, Color color, GermRecord type) {
        super(x, y, color, type);
        cooldown = new Gauge(type.cooldown(), type.cooldown());
    }

    public boolean hasCooldown() {
        return true;
    }

    private boolean isReady() {
        return cooldown.isEmpty();
    }

    private void reset() {
        cooldown.fill();
    }

    public void triggerIfReady(Level lvl) {
        if (isReady()) {
            trigger(lvl);
            reset();
            lvl.updateGrid();
        }
    }

    public void decreaseCooldown() {
        cooldown.decrease();
    }

    @Override
    public void render(float x, float y, float width, float height) {
        super.render(x, y, width, height);
        cooldown.renderCircled(x + width, y + width, dim.tileSize / 5, 10,
          new com.badlogic.gdx.graphics.Color(0, 0, 0, 0),
          cooldown.value() < 3 ?
          new com.badlogic.gdx.graphics.Color(1, 0.25f, 0.35f, 0.5f) :
          new com.badlogic.gdx.graphics.Color(1, 1, 1, 0.5f)
        );
        tra15.drawText(
          cooldown.value() + "",
          x + width - dim.tileSize / 5,
          y + height - dim.tileSize / 5,
          dim.tileSize / 2.5f,
          dim.tileSize / 2.5f
        );
    }
}
