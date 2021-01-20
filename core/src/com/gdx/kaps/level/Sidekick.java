package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gdx.kaps.level.grid.Color;

import java.util.Set;

public enum Sidekick {
    // TODO: add powers
    SEAN   (Color.COLOR_1, "Sean"),    // INFO: Hits a tile then its neighbors
    ZYRAME (Color.COLOR_2, "Zyrame"),  // INFO: Hits 2 random germs
    RED    (Color.COLOR_3, "Red"),     // INFO: Slices a.random column
    MIMAPS (Color.COLOR_4, "Mimaps"),  // INFO: Hits 3 random.tiles
    PAINT  (Color.COLOR_5, "Paint"),   // INFO: Paints 5 tiles in mate's color
    XERETH (Color.COLOR_6, "Xereth"),  // INFO: Slices two.diagonals
    JIM    (Color.COLOR_10, "Jim"),    // INFO: Slices a.random line
    COLOR  (Color.COLOR_11, "Color"),  // INFO: Generates a.single-colored gelule
    // TODO: sidekick that generates a single Caps ?
    ;

    private final Sprite sprite;
    private final Color type;

    Sidekick(Color type, String name) {
        this.type = type;
        sprite = new Sprite(new Texture("android/assets/img/sidekicks/" + name + "_0.png"));
        sprite.flip(false, true);
    }

    public static Color random(Set<Sidekick> sidekicks) {
        if (sidekicks.isEmpty()) {
            throw new IllegalArgumentException("Can't get a type from empty set.");
        }
        return sidekicks.stream()
                 .findAny()
                 .get().type
          ;
    }

    public Color color() {
        return type;
    }

    public Sprite sprite() {
        return sprite;
    }
}
