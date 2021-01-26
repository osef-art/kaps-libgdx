package com.gdx.kaps.level.grid.germ;

import com.gdx.kaps.level.grid.Color;

public class VirusGerm extends GermCooldown {
    public VirusGerm(int x, int y, Color color) {
        super(x, y, color, GermRecord.VIRUS);
    }
}
