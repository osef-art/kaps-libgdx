package com.gdx.kaps.level.sidekick;

import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.renderer.Animated;

public class Sidekick extends Animated {
    private final SidekickRecord type;

    public Sidekick(SidekickRecord record) {
        super("android/assets/img/sidekicks/" + record.path() + "_", 4, 250_000_000);
        type = record;
    }

    public Color color() {
        return type.color();
    }
}
