package com.gdx.kaps.level.grid.caps;

import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.renderer.AnimatedSprite;
import com.gdx.kaps.renderer.Zone;

import static com.gdx.kaps.MainScreen.dim;

public class PoppingCaps extends Caps {
    private final AnimatedSprite sprite;

    public PoppingCaps(GridObject o) {
        super(o.x(), o.y(), o.color());
        sprite = AnimatedSprite.oneShot(
          "android/assets/img/" + color().id() + "/caps/pop_",
          8, 100_000_000
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

    @Override
    public void render() {
        sprite.render(
          dim.gridMargin + x() * dim.get(Zone.TILE).height,
          dim.topTile(y()),
          dim.get(Zone.TILE).width,
          dim.get(Zone.TILE).height
        );
    }
}
