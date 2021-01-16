package com.gdx.kaps.level.grid.germ;

import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Color;

public class BasicGerm extends Germ {
    BasicGerm(int x, int y, Level lvl) {
        super(x, y, Color.random(lvl.colors()), 1, lvl.grid());
    }
}
