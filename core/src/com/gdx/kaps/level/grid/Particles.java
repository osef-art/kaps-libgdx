package com.gdx.kaps.level.grid;

import com.badlogic.gdx.math.Vector2;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.sidekick.Sidekick;
import com.gdx.kaps.renderer.StaticRenderable;
import com.gdx.kaps.renderer.Zone;
import com.gdx.kaps.time.Timer;

import java.util.*;

import static com.gdx.kaps.MainScreen.dim;
import static com.gdx.kaps.MainScreen.sra;

public class Particles implements StaticRenderable {
    private static class Particle implements StaticRenderable {
        private final Timer updateTimer = new Timer(2_500_000 + new Random().nextInt(15_000_000));
        private final Vector2 dest;
        private final Vector2 pos;
        private final Color color;

        public Particle(GridObject o, int n) {
            this.color = o.color();
            pos = new Vector2(
              dim.gridMargin + o.x() * dim.get(Zone.TILE).width + dim.get(Zone.TILE).width / 2,
              dim.topTile(o.y()) + dim.get(Zone.TILE).height / 2
            );
            dest = new Vector2(
              dim.get(Zone.SIDE_PANEL).x + dim.sidekickPanelHeight / 2,
              dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * n) + dim.sidekickPanelHeight - 30
            );
        }

        private boolean isArrived() {
            return Math.abs(dest.x - pos.x) < 10 && Math.abs(dest.y - pos.y) < 10;
        }

        @Override
        public void update() {
            if (updateTimer.resetIfExceeds()) {
                pos.x += (dest.x - pos.x) / 20;
                pos.y += (dest.y - pos.y) / 20;
            }
        }

        @Override
        public void render() {
            sra.drawCircle(pos.x, pos.y, 15, color.value(0.25f));
            sra.drawCircle(pos.x, pos.y, 5, color.value());
        }
    }
    private final List<Particle> particles = new ArrayList<>();
    private final HashMap<Color, Integer> order = new HashMap<>();

    public Particles(List<Sidekick> sidekicks) {
        Objects.requireNonNull(sidekicks);
        for (int i = 0; i < sidekicks.size(); i++) {
            order.put(sidekicks.get(i).color(), i);
        }
    }

    public void add(Set<GridObject> set) {
        set.stream()
          .filter(GridObjectInterface::isDestroyed)
          .forEach(this::add);
    }

    private void add(GridObject o) {
        if (!order.containsKey(o.color())) return;
        particles.add(new Particle(o, order.get(o.color())));
    }

    @Override
    public void update() {
        particles.forEach(p -> {
            p.update();
            if (p.isArrived()) Level.sidekickOfColor(p.color).ifPresent(Sidekick::increaseMana);
        });
        particles.removeIf(Particle::isArrived);
    }

    @Override
    public void render() {
        particles.forEach(Particle::render);
    }
}
