package com.gdx.kaps.level.grid.caps;

import com.badlogic.gdx.math.Rectangle;
import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.level.grid.germ.Germ;
import com.gdx.kaps.renderer.Animated;
import com.gdx.kaps.renderer.AnimatedSprite;
import com.gdx.kaps.renderer.Zone;

import static com.gdx.kaps.MainScreen.dim;

public class EffectAnim implements Animated {
    public enum EffectType {
        FIRE_FX("fire", 8),
        SLICE_FX("slice", 8),
        PAINT_FX("paint", 8),
        CORE_FX("pain", 8),
        ;

        private final String path;
        private final int frames;

        EffectType(String name, int frames) {
            path = "android/assets/img/fx/" + name + "_";
            this.frames = frames;
        }

    }
    private final AnimatedSprite sprite;
    private final Rectangle zone;

    public EffectAnim(EffectType type, int x, int y) {
        this(type.path, type.frames, x, y, 1.5f);
    }

    private EffectAnim(String path, int frames, int x, int y, float scale) {
        sprite = AnimatedSprite.oneShot(path, frames, 100_000_000);
        float width = dim.get(Zone.TILE).width * scale;
        float height = dim.get(Zone.TILE).height * scale;
        zone = new Rectangle(
          dim.gridMargin + x * dim.get(Zone.TILE).width + (dim.get(Zone.TILE).width - width)/2,
          dim.topTile(y) + (dim.get(Zone.TILE).height - height)/2,
          width, height
        );
    }

    public static EffectAnim ofPopping(GridObject o) {
        var path = "android/assets/img/" + o.color().id() + "/"+
                     (o.isGerm() ? "germs/" + ((Germ) o).typeName() : "caps") +
                     "/pop_";
        return new EffectAnim(path, 8, o.x(), o.y(), 1);
    }

    public static EffectAnim ofPainted(GridObject o) {
        return new EffectAnim("android/assets/img/" + o.color().id() + "/caps/pop_", 8, o.x(), o.y(), 1.5f);
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
        sprite.render(zone);
    }
}
