package com.gdx.kaps.level.grid.germ;

import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.GridObject;

public class VirusGerm extends GermCooldown {
    public VirusGerm(int x, int y, Color color) {
        super(x, y, color, GermRecord.VIRUS);
    }

    public VirusGerm(GridObject obj) {
        this(obj.x(), obj.y(), obj.color());
    }
}
