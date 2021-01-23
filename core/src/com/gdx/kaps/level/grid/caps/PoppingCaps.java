package com.gdx.kaps.level.grid.caps;

import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.renderer.AnimatedSprite;

public class PoppingCaps extends Caps {
    private final AnimatedSprite sprite;

    public PoppingCaps(GridObject o) {
        super(o.x(), o.y(), o.color());
        sprite = new AnimatedSprite(
          "android/assets/img/" + color().id() + "/caps/pop_",
          0, 8, 100_000_000
        );
    }

    @Override
    public boolean isDestroyed() {
        return sprite.isOver();
    }

    @Override
    public void update() {
        sprite.update();
    }
}
