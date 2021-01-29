package com.gdx.kaps.level.grid.caps;

import com.badlogic.gdx.math.Rectangle;
import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.level.grid.germ.Germ;
import com.gdx.kaps.renderer.Animated;
import com.gdx.kaps.renderer.AnimatedSprite;

import static com.gdx.kaps.MainScreen.dim;
import static java.util.Objects.requireNonNull;

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
    public static class EffectAnimBuilder {
        private final String path;
        private Rectangle zone;
        private double speed = 100_000_000;
        private int frames = 8;

        public EffectAnimBuilder(EffectType type, Rectangle zone) {
            this(requireNonNull(type).path, zone);
        }

        public EffectAnimBuilder(String path, Rectangle zone) {
            requireNonNull(path);
            requireNonNull(zone);
            this.zone = zone;
            this.path = path;
        }

        public EffectAnimBuilder withSpeed(double speed) {
            this.speed = speed;
            return this;
        }

        public EffectAnimBuilder scaledBy(float scale) {
            zone = new Rectangle(
              zone.x + (zone.width - zone.width * scale) / 2,
              zone.y + (zone.height - zone.height * scale) / 2,
              zone.width * scale,
              zone.height * scale
            );
            return this;
        }

        public EffectAnimBuilder setFrames(int frames) {
            this.frames = frames;
            return this;
        }

        public EffectAnim build() {
            if (speed <= 0) throw new IllegalArgumentException("Invalid animation speed: " + speed);
            if (frames <= 1) throw new IllegalArgumentException("Invalid number of frames for '" + path + "': " + speed);
            return new EffectAnim(requireNonNull(path), frames, speed, requireNonNull(zone));
        }
    }
    private final AnimatedSprite sprite;
    private final Rectangle zone;

    // init

    private EffectAnim(String path, int frames, double speed, Rectangle zone) {
        sprite = AnimatedSprite.oneShot(path, frames, speed);
        this.zone = zone;
    }

    public static EffectAnim ofPopping(GridObject o) {
        return new EffectAnimBuilder(
          "android/assets/img/" + o.color().id() + "/" +
            (o.isGerm() ? "germs/" + ((Germ) o).typeName() : "caps") + "/pop_",
          dim.getTile(o)
        ).build();
    }

    public static EffectAnim ofPainted(GridObject o) {
        return new EffectAnimBuilder(
          "android/assets/img/" + o.color().id() + "/caps/pop_",
          dim.getTile(o)
        ).scaledBy(1.25f).build();
    }

    public static EffectAnim onTile(EffectType type, int x, int y) {
        return new EffectAnimBuilder(type.path, dim.getTile(x, y))
          .scaledBy(1.5f)
          .setFrames(type.frames)
          .build();
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
