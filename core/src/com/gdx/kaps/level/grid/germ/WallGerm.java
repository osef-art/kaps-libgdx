package com.gdx.kaps.level.grid.germ;

import com.gdx.kaps.level.grid.Color;

public class WallGerm extends Germ {
    WallGerm(int x, int y, Color color) {
        super(x, y, color, GermRecord.WALL);
    }

    WallGerm(int x, int y, Color color, int health) {
        super(x, y, color, GermRecord.WALL, health);
    }
}
