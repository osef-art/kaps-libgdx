package com.gdx.kaps.level.grid.germ;

import com.gdx.kaps.level.Gauge;
import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.renderer.Zone;

import static com.gdx.kaps.MainScreen.dim;

public abstract class GermCooldown extends Germ {
    private final Gauge cooldown;

    GermCooldown(int x, int y, Color color, GermRecord type) {
        super(x, y, color, type);
        cooldown = new Gauge(type.cooldown(), type.cooldown());
    }

    @Override
    public void render(float x, float y, float width, float height) {
        super.render(x, y, width, height);
        cooldown.renderCircled(x + width, y + width, dim.get(Zone.TILE).width / 5, 10,
          new com.badlogic.gdx.graphics.Color(0, 0, 0, 0),
          new com.badlogic.gdx.graphics.Color(1, 1, 1, 0.5f)
        );
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

    public void triggerIfReady(Grid grid) {
        if (isReady()) {
            trigger(grid);
            reset();
        }
    }

    public void decreaseCooldown() {
        cooldown.decrease();
    }

}
