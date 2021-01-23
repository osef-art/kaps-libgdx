package com.gdx.kaps.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.level.grid.caps.Gelule;
import com.gdx.kaps.level.grid.caps.PoppingCaps;
import com.gdx.kaps.level.grid.caps.PreviewGelule;
import com.gdx.kaps.level.sidekick.Sidekick;
import com.gdx.kaps.renderer.Renderable;
import com.gdx.kaps.renderer.Zone;
import com.gdx.kaps.time.Timer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gdx.kaps.MainScreen.*;
import static com.gdx.kaps.Sound.play;

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

    // control

    public void moveGeluleLeft() {
        if (!gelule.moveLeftIfPossible(grid)) play("cant");
        updatePreview();
    }

    public void moveGeluleRight() {
        if (!gelule.moveRightIfPossible(grid)) play("cant");
        updatePreview();
    }

    public void dipGelule() {
        if (!gelule.dipIfPossible(grid)) acceptGelule();
    }

    public void flipGelule() {
        var sound = gelule.flipIfPossible(grid) ? "flip" : "cant";
        play(sound);
        updatePreview();
    }

    public void dropGelule() {
        play("drop");
        while (gelule.dipIfPossible(grid));
        acceptGelule();
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

    public void applyToGrid(Consumer<Grid> function) {
        function.accept(grid);
    }

    public void setNext(int n, Gelule gelule) {
        next[n-1] = gelule;
    }

    private void spawnNewGelule() {
        if (gelule != null) return;
        gelule = next[0].copy();
        updateNext();
        updatePreview();

        canHold = true;
        multiplier = 1;
        // TODO: display gelule when game over. maybe in main loop ?

        checkGameOver();
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

                //TODO: ANIM
                // FIXME: last try:
                //  'animMode' boolean in MainScreen
                //  when true, render the anim, else continue the main loop -> one method per anim :x
                var popping = matches.stream()
                                .map(PoppingCaps::new)
                                .collect(Collectors.toList());

                while (!popping.isEmpty()) {
                    Gdx.gl.glClearColor(0.3f, 0.3f, 0.4f, 1);
                    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                    render();

                    popping.forEach(caps -> {
                        caps.update();
                        caps.render();
                    });
                    popping = popping.stream()
                                .filter(caps -> !caps.isDestroyed())
                                .collect(Collectors.toList());
                }

                triggerSidekicks();
                grid.dropAll();
                multiplier++;
            }
        } while (!matches.isEmpty());
    }

    public void checkGameOver() {
        boolean defeat = !gelule.isAtValidEmplacement(grid),
          victory = grid.remainingGerms() <= 0;
        if (defeat || victory) {
            play(defeat ? "game_over" : "cleared");
            render();
            Gdx.graphics.requestRendering();

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
            System.out.println("GAME OVER");
            System.exit(0);
        }
    }

    // rendering

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

        // level
        grid.render();
        Optional.ofNullable(preview).ifPresent(Gelule::render);
        Optional.ofNullable(gelule).ifPresent(Gelule::render);

        next[0].render(dim.get(Zone.NEXT_GELULE));
        Optional.ofNullable(hold).ifPresent(hold -> hold.render(dim.get(Zone.HOLD_GELULE)));
    }

    @Override
    public void render(float x, float y, float width, float height) {
    }
}
