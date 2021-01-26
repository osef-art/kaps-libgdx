package com.gdx.kaps.level.grid.caps;

import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.level.grid.Position;
import com.gdx.kaps.renderer.AnimatedSprite;
import com.gdx.kaps.renderer.StaticRenderable;
import com.gdx.kaps.renderer.Zone;

import static com.gdx.kaps.MainScreen.dim;

public class EffectAnim implements StaticRenderable {
    // IMPL: intend to implement classic Renderable
    public enum EffectType {
        FIRE("fire", 8),
        SLICE("slice", 8),
        PAINT("paint", 8),
        CORE("pain", 8),
        ;

        private final String path;
        private final int frames;

        EffectType(String name, int frames) {
            path = "android/assets/img/fx/" + name + "_";
            this.frames = frames;
        }

    }
    private final AnimatedSprite sprite;
    private final Position position;
    private final float scale;

    public EffectAnim(EffectType type, int x, int y) {
        this(type.path, type.frames, x, y, 1.5f);
    }

    // IMPL: coherent args order ?
    private EffectAnim(String path, int frames, int x, int y, float scale) {
        sprite = AnimatedSprite.oneShot(path, frames, 100_000_000);
        position = new Position(x, y);
        this.scale = scale;
    }

    public static EffectAnim ofPopping(GridObject o) {
        return new EffectAnim("android/assets/img/" + o.color().id() + "/caps/pop_", 8, o.x(), o.y(), 1);
    }

    public boolean isOver() {
        return sprite.isOver();
    }

    @Override
    public void update() {
        sprite.update();
    }

    @Override
    public void render() {
        float width = dim.get(Zone.TILE).width * scale;
        float height = dim.get(Zone.TILE).height * scale;
        sprite.render(
          dim.gridMargin + position.x() * dim.get(Zone.TILE).width + (dim.get(Zone.TILE).width - width)/2,
          dim.topTile(position.y()) + (dim.get(Zone.TILE).height - height)/2,
          width,
          height
        );
    }
}
