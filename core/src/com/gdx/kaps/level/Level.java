package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Color;
import com.gdx.kaps.level.grid.caps.PreviewGelule;
import com.gdx.kaps.renderer.Renderable;
import com.gdx.kaps.level.grid.caps.Gelule;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.renderer.Zone;
import com.gdx.kaps.time.Timer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gdx.kaps.MainScreen.*;

public class Level implements Renderable {
    // INFO: temporary renderer
    public final static int MIN_MATCH_RANGE = 4;
    private int updateSpeed = 1_000_000_000;
    private final Timer updateTimer;
    private final List<Sidekick> sidekicks;
    private final Set<com.gdx.kaps.level.grid.Color> colors;
    private final Grid grid;
    private boolean canHold;
    private PreviewGelule preview;
    private Gelule gelule;
    private Gelule next;
    private Gelule hold;

    public Level(Path filePath, Set<Sidekick> sidekicks) {
        Objects.requireNonNull(sidekicks);
        Objects.requireNonNull(filePath);

        this.sidekicks = new ArrayList<>(sidekicks);
        colors =
          Stream.concat(
            this.sidekicks.stream().map(Sidekick::color),
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

    // control

    public void moveGeluleLeft() {
        gelule.moveLeftIfPossible(grid);
        updatePreview();
    }

    public void moveGeluleRight() {
        gelule.moveRightIfPossible(grid);
        updatePreview();
    }

    public void dipGelule() {
        if (!gelule.dipIfPossible(grid)) acceptGelule();
    }

    public void flipGelule() {
        gelule.flipIfPossible(grid);
        updatePreview();
    }

    public void dropGelule() {
        while (gelule.dipIfPossible(grid));
        acceptGelule();
    }

    // update

    private void updatePreview() {
        preview = new PreviewGelule(gelule);
        while (preview.dipIfPossible(grid));
    }

    private void spawnNewGelule() {
        if (gelule != null) return;
        gelule = next.copy();
        updatePreview();

        next = new Gelule(this);
        canHold = true;
        // TODO: display gelule when game over. maybe in main loop ?
        checkGameOver();
    }

    private void acceptGelule() {
        grid.accept(gelule);
        preview = null;
        gelule = null;

        updateGrid();
        speedUp();
        spawnNewGelule();
    }

    public void update() {
        if (updateTimer.resetIfExceeds()) dipGelule();
    }

    private void speedUp() {
        updateSpeed -= 100;
        updateTimer.updateLimit(updateSpeed);
        updateTimer.reset();
    }

    public void checkGameOver() {
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

    public void hold() {
        if (!canHold) return;
        var tmp = Optional.ofNullable(hold);
        hold = Gelule.copyColorFrom(gelule, new Gelule(this));
        tmp.ifPresentOrElse(
          hold -> gelule = Gelule.copyColorFrom(hold, gelule),
          () -> {
            gelule = null;
            spawnNewGelule();
          }
        );
        canHold = false;
    }

    private void updateGrid() {
        while (grid.deleteMatches()) {
            grid.dropAll();
        }
    }

    @Override
    public void render() {
        // background
        sra.drawRect(
          dim.gridMargin,
          dim.gridMargin + dim.get(Zone.GRID).height + 10,
          dim.get(Zone.GRID).width,
          dim.gridMargin - 10,
          new Color(0.5f, 0.5f, 0.65f, 1)
        );
        sra.drawRect(
          dim.gridMargin,
          dim.gridMargin + dim.get(Zone.GRID).height + 10,
          (float) (dim.get(Zone.GRID).width * updateTimer.ratio()),
          dim.gridMargin - 10,
          new Color(0.6f, 0.6f, 0.75f, 1)
        );

        sra.drawRect(
          dim.get(Zone.SIDE_PANEL),
          new Color(0.4f, 0.45f, 0.55f, 1)
        );

        sra.drawRect(
          dim.get(Zone.NEXT_BOX),
          new Color(0.45f, 0.5f, 0.6f, 1)
        );
        tra.drawText("NEXT", dim.get(Zone.NEXT_BOX).x, dim.get(Zone.NEXT_BOX).y + dim.get(Zone.NEXT_BOX).height + 10);

        sra.drawRect(
          dim.get(Zone.HOLD_BOX),
          new Color(0.45f, 0.5f, 0.6f, 1)
        );
        tra.drawText("HOLD", dim.get(Zone.HOLD_BOX).x, dim.get(Zone.HOLD_BOX).y + dim.get(Zone.HOLD_BOX).height + 10);

        for (int i = 0; i < sidekicks.size(); i++) {
            var sdk = sidekicks.get(i);
            sra.drawRect(
              dim.get(Zone.SIDE_PANEL).x,
              dim.gridMargin * (6 + i) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * i),
              dim.get(Zone.SIDE_PANEL).width,
              dim.get(Zone.NEXT_BOX).height / 2,
              new Color(0.45f, 0.5f, 0.6f, 1)
            );
            batch.begin();
            batch.draw(
              sdk.sprite(),
              dim.get(Zone.SIDE_PANEL).x + 5,
              dim.gridMargin * (6 + i) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * i) + 5,
              dim.get(Zone.NEXT_BOX).height / 2 - 10,
              dim.get(Zone.NEXT_BOX).height / 2 - 10
            );
            batch.end();
        }

        sra.drawRect(
          dim.get(Zone.BOTTOM_PANEL),
          new Color(0.6f, 0.45f, 0.85f, 1)
        );

        // level
        grid.render();
        Optional.ofNullable(preview).ifPresent(Gelule::render);
        Optional.ofNullable(gelule).ifPresent(Gelule::render);

        next.render(dim.get(Zone.NEXT_GELULE));
        Optional.ofNullable(hold).ifPresent(hold -> hold.render(dim.get(Zone.HOLD_GELULE)));
    }
}
