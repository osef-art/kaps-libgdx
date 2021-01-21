package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Color;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.level.grid.caps.Gelule;
import com.gdx.kaps.level.grid.caps.PreviewGelule;
import com.gdx.kaps.level.sidekick.Sidekick;
import com.gdx.kaps.renderer.Renderable;
import com.gdx.kaps.renderer.Zone;
import com.gdx.kaps.time.Timer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gdx.kaps.MainScreen.*;

public class Level implements Renderable {
    public final static int MIN_MATCH_RANGE = 4;
    private int updateSpeed = 1_000_000_000;
    private final Timer updateTimer;
    private int multiplier = 1;
    private int score;
    private final List<Sidekick> sidekicks;
    private final Set<com.gdx.kaps.level.grid.Color> colors;
    private final Grid grid;
    private boolean paused;
    private boolean canHold;
    private PreviewGelule preview;
    private Gelule gelule;
    private Gelule next;
    private Gelule hold;

    public Level(Path filePath, Set<Sidekick> sidekicks) {
        Objects.requireNonNull(sidekicks);
        Objects.requireNonNull(filePath);

        this.sidekicks = new ArrayList<>(sidekicks);

        colors = Stream.concat(
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

    private Optional<Sidekick> sidekickOfColor(com.gdx.kaps.level.grid.Color color) {
        return sidekicks.stream()
                 .filter(sdk -> sdk.color() == Objects.requireNonNull(color))
                 .findAny();
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

    public void togglePause() {
        paused = !paused;
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
        multiplier = 1;
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

    private void triggerSidekicks() {

    }

    @Override
    public void update() {
        if (updateTimer.resetIfExceeds()) dipGelule();
        grid.update();
        sidekicks.forEach(Renderable::update);
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
        Set<GridObject> matches;
        do {
            matches = grid.deleteMatches();
            matches.forEach(o -> {
                sidekickOfColor(o.color()).ifPresent(s -> s.gauge().increase());
                score += o.points() * multiplier;
            });
            triggerSidekicks();
            grid.dropAll();
            multiplier++;
        } while (!matches.isEmpty());
    }

    private void renderBackGround() {
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

        sra.drawRect(
          dim.get(Zone.BOTTOM_PANEL),
          new Color(0.6f, 0.45f, 0.85f, 1)
        );

        tra.drawCenteredText(score + "", dim.get(Zone.BOTTOM_PANEL));
        tra.drawCenteredText( "score:",
          dim.get(Zone.BOTTOM_PANEL).x,
          dim.get(Zone.BOTTOM_PANEL).y,
          dim.get(Zone.BOTTOM_PANEL).width,0
        );

    }

    private void renderSidekicks() {
        for (int i = 0; i < sidekicks.size(); i++) {
            var sdk = sidekicks.get(i);
            sra.drawRect(
              dim.get(Zone.SIDE_PANEL).x,
              dim.gridMargin * (6 + i) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * i),
              dim.get(Zone.SIDE_PANEL).width,
              dim.sidekickPanelHeight,
              new Color(0.45f, 0.5f, 0.6f, 1)
            );

            sdk.render(
              dim.get(Zone.SIDE_PANEL).x + 5,
              dim.gridMargin * (6 + i) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * i) + 5,
              dim.sidekickPanelHeight - 10,
              dim.sidekickPanelHeight - 10
            );

            // gauge
            sdk.gauge().render(
              dim.get(Zone.SIDE_PANEL).x + 10 + dim.sidekickPanelHeight - 10,
              dim.gridMargin * (6 + i) + dim.get(Zone.NEXT_BOX).height  * (2 + 0.5f * i) + dim.sidekickPanelHeight - 30,
              dim.get(Zone.SIDE_PANEL).width - (dim.sidekickPanelHeight - 10) - 15,
              20,
              new Color(0.5f, 0.55f, 0.65f, 1),
              sdk.color().value()
            );

            tra.drawText(
              sdk.gauge().value() + " / " + sdk.gauge().max(),
              dim.get(Zone.SIDE_PANEL).x + 10 + dim.sidekickPanelHeight - 10,
              dim.gridMargin * (6 + i) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * i) + 10
            );
        }
    }

    @Override
    public void render() {
        renderBackGround();
        renderSidekicks();

        // level
        grid.render();
        Optional.ofNullable(preview).ifPresent(Gelule::render);
        Optional.ofNullable(gelule).ifPresent(Gelule::render);

        next.render(dim.get(Zone.NEXT_GELULE));
        Optional.ofNullable(hold).ifPresent(hold -> hold.render(dim.get(Zone.HOLD_GELULE)));
    }

    @Override
    public void render(float x, float y, float width, float height) {
    }
}
