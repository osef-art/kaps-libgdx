package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Color;
import com.gdx.kaps.SoundStream;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.level.grid.Particles;
import com.gdx.kaps.level.grid.caps.EffectAnim;
import com.gdx.kaps.level.grid.caps.Gelule;
import com.gdx.kaps.level.grid.caps.PreviewGelule;
import com.gdx.kaps.level.sidekick.Sidekick;
import com.gdx.kaps.level.sidekick.SidekickRecord;
import com.gdx.kaps.renderer.Animated;
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
import static com.gdx.kaps.Utils.getRandomFrom;
import static java.util.stream.Collectors.toList;

public class Level implements Animated {
    private int updateSpeed = 1_000_000_000;
    public final static int MIN_MATCH_RANGE = 4;
    private static List<Sidekick> sidekicks;
    private static Particles particles;
    private static int multiplier = 1;
    private static int score;
    private final Timer updateTimer;
    private final SoundStream stream = new SoundStream();
    private final Set<com.gdx.kaps.level.grid.Color> colors;
    private static final List<EffectAnim> effects = new ArrayList<>();
    private final Grid grid;
    private boolean paused = true;
    private boolean canHold;
    private PreviewGelule preview;
    private final Gelule[] next = new Gelule[2];
    private Gelule gelule;
    private Gelule hold;
    // TODO: list of controllable Unlinked caps

    public Level(Path filePath, Set<Sidekick> sdks) {
        Objects.requireNonNull(sdks);
        Objects.requireNonNull(filePath);

        sidekicks = List.copyOf(sdks);

        colors = Stream.concat(
          sidekicks.stream().map(Sidekick::color),
          Stream.of(com.gdx.kaps.level.grid.Color.randomBlank())
        ).collect(Collectors.toUnmodifiableSet());

        try {
            grid = Grid.parseLevel(filePath, colors);
        } catch (IOException e) {
            throw new AssertionError("Error when parsing file " + filePath + ": " + e);
        }

        for (int i = 0; i < next.length; i++) {
            next[i] = new Gelule(this);
        }
        updateTimer = new Timer(updateSpeed);
        particles = new Particles(sidekicks);
        spawnNewGelule();
    }

    // getters

    public int gridWidth() {
        return grid.width();
    }

    public int gridHeight() {
        return grid.height();
    }

    public com.gdx.kaps.level.grid.Color randomColor() {
        return com.gdx.kaps.level.grid.Color.random(colors);
    }

    public List<Sidekick> sidekicks() {
        return sidekicks;
    }

    public static Optional<Sidekick> sidekickOfColor(com.gdx.kaps.level.grid.Color color) {
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

    public boolean isPaused() {
        return paused;
    }

    // control
    private void doIfPresent(Consumer<Gelule> action) {
        currentGelule().ifPresent(action);
    }

    public void moveGeluleLeft() {
        doIfPresent(gelule -> {
            if (!gelule.moveLeftIfPossible(grid)) stream.play("cant");
            updatePreview();
        });
    }

    public void moveGeluleRight() {
        doIfPresent(gelule -> {
            if (!gelule.moveRightIfPossible(grid)) stream.play("cant");
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
            stream.play(sound);
            updatePreview();
        });
    }

    public void dropGelule() {
        doIfPresent(gelule -> {
            stream.play("drop");
            while (gelule.dipIfPossible(grid));
            acceptGelule();
        });
    }

    public void togglePause() {
        if (!paused) stream.play("pause");
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
        stream.play("hold");

        canHold = false;
    }


    // operations
    public <T> void applyToGrid(BiConsumer<Grid, ? super T> function, T actor) {
        function.accept(grid, actor);
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
        stream.play("impact");
        preview = null;
        gelule = null;

        updateGrid();
        triggerSidekicks();
        decreaseCooldowns();
        speedUp();
        spawnNewGelule();
    }

    private void triggerSidekicks() {
        // TODO: animation focus when sidekicks are triggered
        sidekicks.forEach(sdk -> sdk.triggerIfReady(this));
    }

    private void decreaseCooldowns() {
        sidekicks.forEach(Sidekick::decreaseCooldown);
        triggerSidekicks();
        grid.everyGermsWithCooldowns()
          .forEach(g -> {
              g.decreaseCooldown();
              g.triggerIfReady(this);
          });
    }

    private void speedUp() {
        updateSpeed *= 0.996;
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
        particles.update();
        sidekicks.forEach(Animated::update);
        effects.forEach(EffectAnim::update);
        effects.removeIf(EffectAnim::isOver);
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

    public void updateGrid() {
        Matches matches;
        do {
            grid.dropAll();
            matches = grid.hitMatches()
                        .peek((color, set) -> {
                            var sound = "plop0";
                            if (set.size() > MIN_MATCH_RANGE) {
                                particles.add(set);
                                sidekickOfColor(color).ifPresent(Sidekick::decreaseCooldown);
                                sound = "match_five";
                            }
                            else if (!sidekickOfColor(color).map(Sidekick::hasCooldown).orElse(true)) {
                                particles.add(set);
                            }
                            stream.play(sound);
                        });
            multiplier++;
        } while (!matches.isEmpty());
    }

    public void end() {
        boolean victory = grid.remainingGerms() == 0;
        stream.play(victory ? "cleared" : "game_over");

        System.out.println(victory ? "LEVEL CLEARED!" : "GAME OVER");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new AssertionError(e);
        }
        System.exit(0);
    }

    public static void increaseScore(int points) {
        score += points * multiplier;
    }

    // rendering

    public static void addParticle(GridObject o) {
        particles.add(o);
    }

    public static void addEffect(EffectAnim.EffectType type, int x, int y) {
        effects.add(EffectAnim.onTile(type, x, y));
    }

    public static void addEffect(EffectAnim effect) {
        effects.add(effect);
    }

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
        tra25.drawText("NEXT", dim.get(Zone.NEXT_BOX).x, dim.get(Zone.NEXT_BOX).y + dim.get(Zone.NEXT_BOX).height + 10);

        sra.drawRect(
          dim.get(Zone.HOLD_BOX),
          new Color(0.45f, 0.5f, 0.6f, 1)
        );
        tra25.drawText("HOLD", dim.get(Zone.HOLD_BOX).x, dim.get(Zone.HOLD_BOX).y + dim.get(Zone.HOLD_BOX).height + 10);

        for (int n = 0; n < sidekicks.size(); n++) {
            // fond
            sra.drawRect(
              dim.get(Zone.SIDE_PANEL).x,
              dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * n),
              dim.get(Zone.SIDE_PANEL).width,
              dim.sidekickPanelHeight,
              sidekicks.get(n).hasCooldown() ?
              new com.badlogic.gdx.graphics.Color(0.35f, 0.4f, 0.5f, 1) :
              new com.badlogic.gdx.graphics.Color(0.45f, 0.5f, 0.6f, 1)
            );
        }

        // bottom panel
        sra.drawRect(
          dim.get(Zone.BOTTOM_PANEL),
          sidekicks.get(0).color().value()
        );

        tra25.drawText(score + "", dim.get(Zone.BOTTOM_PANEL));
        tra25.drawText( "score:",
          dim.get(Zone.BOTTOM_PANEL).x,
          dim.get(Zone.BOTTOM_PANEL).y,
          dim.get(Zone.BOTTOM_PANEL).width,
          dim.get(Zone.BOTTOM_PANEL).height/2
        );
    }

    private void renderSidekicks() {
        for (int n = 0; n < sidekicks.size(); n++) {
            var sdk = sidekicks.get(n);
            // gauge
            sdk.renderGauge(n);

            // head
            sdk.render(
              dim.get(Zone.SIDE_PANEL).x + 5,
              dim.gridMargin * (6 + n) + dim.get(Zone.NEXT_BOX).height * (2 + 0.5f * n) + 5,
              dim.sidekickPanelHeight - 10,
              dim.sidekickPanelHeight - 10
            );
        }
    }

    private void renderEffects() {
        if (effects.isEmpty()) return;
            // TODO: different focus for germs
        sra.drawRect(dim.get(Zone.GRID), new Color(0, 0.1f, 0.2f, 0.15f));
        effects.forEach(EffectAnim::render);
    }

    private void renderPauseScreen() {
        if (!paused) return;
        // fond
        sra.drawRect(dim.get(Zone.GRID_PANEL), new Color(0, 0.1f, 0.2f, 0.7f));

        int panelHeight = 200;
        for (int n = 0; n < sidekicks.size(); n++) {
            var sdk = sidekicks.get(n);

            // panel
            sra.drawRect(
              0, dim.gridMargin * 2 + n * (dim.gridMargin + panelHeight),
              dim.get(Zone.GRID_PANEL).width, panelHeight,
              sdk.color().value(0.3f)
            );
            tra25.drawText(sdk.name(), 10, dim.gridMargin * 2 + n * (dim.gridMargin + panelHeight) + 10);

            // head & stars
            sdk.render(10, dim.gridMargin * 2 + n * (dim.gridMargin + panelHeight) + 45, 75, 75);
            for (int s = 0; s < 3; s++) {
                // TODO: draw real stars (sprites ?)
                sra.drawRect(
                  20 * (s + 1),
                  dim.gridMargin * 2 + n * (dim.gridMargin + panelHeight) + 55 + 75, 15, 15,
                  s < sdk.stats().stars() ? Color.WHITE : new Color(1, 1, 1, 0.3f)
                );
            }

            // stats
            for (int i = 0; i < sdk.stats().list().size(); i++) {
                var attr = sdk.stats().list().get(i);
                // attribute
                tra15.drawText(attr.getKey().toUpperCase(), 100, dim.gridMargin * 2 + 55 + n * (dim.gridMargin + panelHeight) + i * 20);
                // points
                for (int p = 0; p < 4; p++) {
                    sra.drawCircle(
                      210 + 20 * p,
                      dim.gridMargin * 2 + 60 + n * (dim.gridMargin + panelHeight) + i * 20,
                      5, p < attr.getValue() ? Color.WHITE : new Color(1, 1, 1, 0.3f)
                    );
                }
            }

            // description
            tra15.drawText(sdk.description(), 10, dim.gridMargin * 2 + n * (dim.gridMargin + panelHeight) + 55 + 75 + 25);

            tra25.drawText(
              "Press 'P' to continue",
              0, dim.get(Zone.BOTTOM_PANEL).y - 100,
              dim.get(Zone.GRID_PANEL).width, 100
            );
        }
    }

    @Override
    public void render() {
        renderBackGround();

        grid.render();
        Optional.ofNullable(gelule).ifPresent(g -> {
            preview.render();
            g.render();
        });

        next[0].render(dim.get(Zone.NEXT_GELULE));
        Optional.ofNullable(hold).ifPresent(hold -> hold.render(dim.get(Zone.HOLD_GELULE)));
        renderPauseScreen();

        renderEffects();
        particles.render();
        renderSidekicks();
    }
}
