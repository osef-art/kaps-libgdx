package com.gdx.kaps.level;

import com.gdx.kaps.Renderable;
import com.gdx.kaps.level.caps.Color;
import com.gdx.kaps.level.caps.Gelule;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Level implements Renderable {
    // INFO: temporary renderer
    private final Set<Color> colors;
    private final Grid grid;
    private Gelule gelule;

    public Level(int width, int height, Set<Sidekick> sidekicks) {
        Objects.requireNonNull(sidekicks);
        // TODO: handle all sidekicks (when they have powers) (strategy)
        colors =
          Stream.concat(
            sidekicks.stream().map(Sidekick::color),
            Stream.of(Color.randomBlank())
          ).collect(Collectors.toSet());

        grid = new Grid(width, height);
        spawnNewGelule();
    }

    private void spawnNewGelule() {
        if (gelule != null) return;
        gelule = new Gelule(grid, colors);
        // TODO: display gelule when game over. maybe in main loop ?

        if (!gelule.isAtValidEmplacement(grid)) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
            System.exit(0);
        }
    }

    private void acceptGelule() {
        grid.accept(gelule);
        gelule = null;
        while (grid.deleteMatches()) {
            grid.dropAll();
        }
        spawnNewGelule();
    }

    // control
    public void moveGeluleLeft() {
        gelule.moveLeft(grid);
    }

    public void moveGeluleRight() {
        gelule.moveRight(grid);
    }

    public void dipGelule() {
        if (!gelule.dip(grid)) acceptGelule();
    }

    public void flipGelule() {
        gelule.flip(grid);
    }

    public void dropGelule() {
        while (gelule.dip(grid));
        acceptGelule();
    }

    public void update() {
        //TODO: drop gelule every x seconds
    }

    @Override
    public void render() {
        grid.render();
        Optional.ofNullable(gelule).ifPresent(Gelule::render);
    }
}
