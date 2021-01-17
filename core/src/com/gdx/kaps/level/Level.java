package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Color;
import com.gdx.kaps.Renderable;
import com.gdx.kaps.level.grid.Gelule;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.time.Timer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gdx.kaps.MainScreen.dim;
import static com.gdx.kaps.MainScreen.sra;

public class Level implements Renderable {
    // INFO: temporary renderer
    public final static int MIN_MATCH_RANGE = 4;    // TODO: find a way better name
    private int updateSpeed = 1_000_000_000;
    private final Timer updateTimer;
    private final Set<com.gdx.kaps.level.grid.Color> colors;
    // TODO: implement next & hold
    private boolean canHold;
    private final Grid grid;
    private Gelule gelule;
    private Gelule next;
    private Gelule hold;

    public Level(Path filePath, Set<Sidekick> sidekicks) {
        Objects.requireNonNull(sidekicks);

        colors =
          Stream.concat(
            sidekicks.stream().map(Sidekick::color),
            Stream.of(com.gdx.kaps.level.grid.Color.randomBlank())
          ).collect(Collectors.toUnmodifiableSet());

        try {
            grid = Grid.parseLevel(filePath, colors);
        } catch (IOException e) {
            throw new AssertionError("Error when parsing file " + filePath + ": " + e);
        }
        updateTimer = new Timer(updateSpeed);
        next = new Gelule(this);
        spawnNewGelule();
    }

    // getters

    public int gridWidth() {
        return grid.width();
    }

    public int gridHeight() {
        return grid.height();
    }

    public Set<com.gdx.kaps.level.grid.Color> colors() {
        return colors;
    }

    private void spawnNewGelule() {
        if (gelule != null) return;
        gelule = next.copy();
        next = new Gelule(this);
        // TODO: display gelule when game over. maybe in main loop ?
        checkGameOver();
    }

    private void acceptGelule() {
        grid.accept(gelule);
        gelule = null;

        updateGrid();
        speedUp();
        spawnNewGelule();
    }

    // control

    public void moveGeluleLeft() {
        gelule.moveLeftIfPossible(grid);
    }

    public void moveGeluleRight() {
        gelule.moveRightIfPossible(grid);
    }

    public void dipGelule() {
        if (!gelule.dipIfPossible(grid)) acceptGelule();
    }

    public void flipGelule() {
        gelule.flipIfPossible(grid);
    }

    public void dropGelule() {
        while (gelule.dipIfPossible(grid));
        acceptGelule();
    }

    // update

    public void update() {
        if (updateTimer.resetIfExceeds()) dipGelule();
    }

    private void speedUp() {
        updateSpeed -= 100;
        updateTimer.updateLimit(updateSpeed);
        updateTimer.reset();
    }

    private void checkGameOver() {
        if (!gelule.isAtValidEmplacement(grid) || grid.remainingGerms() <= 0) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
            System.out.println("GAME OVER");
            System.exit(0);
        }
    }

    private void updateGrid() {
        while (grid.deleteMatches()) {
            grid.dropAll();
        }
    }

    @Override
    public void render() {
        grid.render();
        Optional.ofNullable(gelule).ifPresent(Gelule::render);
        // TODO: render gelule preview

        sra.drawRect(
          dim.gridMargin,
          dim.gridMargin + dim.grid.height + 10,
          dim.grid.width,
          dim.gridMargin - 10,
          new Color(0.5f, 0.5f, 0.65f, 1)
        );
        sra.drawRect(
          dim.gridMargin,
          dim.gridMargin + dim.grid.height + 10,
          (float) (dim.grid.width * updateTimer.ratio()),
          dim.gridMargin - 10,
          new Color(0.6f, 0.6f, 0.75f, 1)
        );

        sra.drawRect(
          dim.sidePanel,
          new Color(0.4f, 0.45f, 0.55f, 1)
        );

        sra.drawRect(
          dim.nextBox,
          new Color(0.45f, 0.5f, 0.6f, 1)
        );
        sra.drawRect(
          dim.holdBox,
          new Color(0.45f, 0.5f, 0.6f, 1)
        );

        sra.drawRect(
          dim.bottomPanel,
          new Color(0.6f, 0.45f, 0.85f, 1)
        );

        next.render(dim.nextGelule);
    }
}
