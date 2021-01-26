package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Color;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.level.grid.caps.Gelule;
import com.gdx.kaps.level.grid.caps.PreviewGelule;
import com.gdx.kaps.level.sidekick.Sidekick;
import com.gdx.kaps.level.sidekick.SidekickRecord;
import com.gdx.kaps.renderer.Renderable;
import com.gdx.kaps.renderer.StaticRenderable;
import com.gdx.kaps.renderer.Zone;
import com.gdx.kaps.time.Timer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gdx.kaps.MainScreen.*;
import static com.gdx.kaps.Sound.play;
import static com.gdx.kaps.Utils.getRandomFrom;
import static java.util.stream.Collectors.toList;

public class Level implements StaticRenderable {
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
    private final Gelule[] next;
    private Gelule gelule;
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

        next = new Gelule[2];
        for (int i = 0; i < next.length; i++) {
            next[i] = new Gelule(this);
        }
        updateTimer = new Timer(updateSpeed);
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

    public List<Sidekick> sidekicks() {
        return sidekicks;
    }

    private Optional<Sidekick> sidekickOfColor(com.gdx.kaps.level.grid.Color color) {
        return sidekicks.stream()
                 .filter(sdk -> sdk.color() == Objects.requireNonNull(color))
                 .findAny();
    }

    public Sidekick getSidekickExcept(SidekickRecord sidekick) {
        return getRandomFrom(
          sidekicks().stream()
            .filter(sdk -> sdk.color() != sidekick.color())
            .collect(toList())
        ).orElse(sidekicks().get(0));
    }

    private Optional<Gelule> currentGelule() {
        return Optional.ofNullable(gelule);
    }

    private boolean isOver() {
        boolean defeat = currentGelule()
                           .map(g -> !g.isAtValidEmplacement(grid))
                           .orElse(false);
        boolean victory = grid.remainingGerms() <= 0;
        boolean noMoreAnims = !grid.hasPoppingCaps();
        return (defeat || victory) && noMoreAnims;
    }

    // control

    private void doIfPresent(Consumer<Gelule> action) {
        currentGelule().ifPresent(action);
    }

    public void moveGeluleLeft() {
        doIfPresent(gelule -> {
            if (!gelule.moveLeftIfPossible(grid)) play("cant");
            updatePreview();
        });
    }

    public void moveGeluleRight() {
        doIfPresent(gelule -> {
            if (!gelule.moveRightIfPossible(grid)) play("cant");
            updatePreview();
        });
    }

    public void dipGelule() {
        doIfPresent(gelule -> {
            if (!gelule.dipIfPossible(grid)) acceptGelule();
        });
    }

    public void flipGelule() {
        doIfPresent(gelule -> {
            var sound = gelule.flipIfPossible(grid) ? "flip" : "cant";
            play(sound);
            updatePreview();
        });
    }

    public void dropGelule() {
        doIfPresent(gelule -> {
            play("drop");
            while (gelule.dipIfPossible(grid));
            acceptGelule();
        });
    }

    public void togglePause() {
        if (!paused) play("pause");
        paused = !paused;
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
        play("hold");

        canHold = false;
    }

    // operations

    public void applyToGrid(BiConsumer<Grid, SidekickRecord> function, SidekickRecord sidekick) {
        function.accept(grid, sidekick);
    }

    public void setNext(int n, Gelule gelule) {
        next[n-1] = gelule;
    }

    private void spawnNewGelule() {
        // TODO: find a way to spawn it only when anims are done
        if (gelule != null || isOver()) return;
        gelule = next[0].copy();

        updateNext();
        updatePreview();

        canHold = true;
        multiplier = 1;
    }

    private void acceptGelule() {
        grid.accept(gelule);
        play("impact");
        preview = null;
        gelule = null;

        updateGrid();
        speedUp();
        decreaseCooldowns();
        spawnNewGelule();
    }

    private void triggerSidekicks() {
        // TODO: animation focus when sidekicks are triggered
        sidekicks.forEach(sdk -> sdk.triggerIfReady(this));
    }

    private void decreaseCooldowns() {
        sidekicks.forEach(sdk -> {
            sdk.decreaseCooldown();
            sdk.triggerIfReady(this);
        });
    }

    private void speedUp() {
        updateSpeed -= 2_500_000;
        updateTimer.updateLimit(updateSpeed);
        updateTimer.reset();
    }

    // update

    @Override
    public void update() {
        if (paused) return;
        // FIXME: update on pause (récup remaining time)
        if (updateTimer.resetIfExceeds()) dipGelule();
        grid.update();
        sidekicks.forEach(Renderable::update);
        if (isOver()) end();
    }

    private void updateNext() {
        System.arraycopy(next, 1, next, 0, next.length - 1);
        next[next.length - 1] = new Gelule(this);
    }

    private void updatePreview() {
        preview = new PreviewGelule(gelule);
        while (preview.dipIfPossible(grid));
    }

    private void updateGrid() {
        Set<GridObject> matches;
        do {
            matches = grid.deleteMatches();
            if (!matches.isEmpty()) {
                play(matches.size() > 4 ? "match_five" : "plop0");

                matches.forEach(o -> {
                    sidekickOfColor(o.color()).ifPresent(Sidekick::increaseMana);
                    score += o.points() * multiplier;
                });
                triggerSidekicks();
                grid.dropAll();
                multiplier++;
            }
            // TODO: match of 5 = decrease cooldown !
        } while (!matches.isEmpty());
    }

    public void end() {
        boolean victory = grid.remainingGerms() == 0;
        play(victory ? "cleared" : "game_over");

        System.out.println(victory ? "LEVEL CLEARED!" : "GAME OVER");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
        System.exit(0);
    }

    // rendering

    private void renderBackGround() {
        // timer
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

        // side panel
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

        // bottom panel
        sra.drawRect(
          dim.get(Zone.BOTTOM_PANEL),
          sidekicks.get(0).color().value()
        );

        tra.drawText(score + "", dim.get(Zone.BOTTOM_PANEL));
        tra.drawText( "score:",
          dim.get(Zone.BOTTOM_PANEL).x,
          dim.get(Zone.BOTTOM_PANEL).y,
          dim.get(Zone.BOTTOM_PANEL).width,0
        );
    }

    private void renderSidekicks() {
        for (int n = 0; n < sidekicks.size(); n++) {
            var sdk = sidekicks.get(n);

            // fond
            sra.drawRect(
              dim.get(Zone.SIDE_PANEL).x,
              dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * n),
              dim.get(Zone.SIDE_PANEL).width,
              dim.sidekickPanelHeight,
              new com.badlogic.gdx.graphics.Color(0.45f, 0.5f, 0.6f, 1)
            );

            // head
            sdk.render(
              dim.get(Zone.SIDE_PANEL).x + 5,
              dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * n) + 5,
              dim.sidekickPanelHeight - 10,
              dim.sidekickPanelHeight - 10
            );

            // gauge
            sdk.renderGauge(n);
        }
    }

    @Override
    public void render() {
        renderBackGround();
        renderSidekicks();

        grid.render();
        Optional.ofNullable(gelule).ifPresent(g -> {
            preview.render();
            g.render();
        });

        next[0].render(dim.get(Zone.NEXT_GELULE));
        Optional.ofNullable(hold).ifPresent(hold -> hold.render(dim.get(Zone.HOLD_GELULE)));
    }
}
