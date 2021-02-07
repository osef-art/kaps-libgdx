package com.gdx.kaps.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gdx.kaps.SoundStream;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.GridObject;
import com.gdx.kaps.level.grid.Particles;
import com.gdx.kaps.level.grid.caps.Caps;
import com.gdx.kaps.level.grid.caps.ControllableGelule;
import com.gdx.kaps.level.grid.caps.EffectAnim;
import com.gdx.kaps.level.grid.caps.Gelule;
import com.gdx.kaps.level.sidekick.Sidekick;
import com.gdx.kaps.level.sidekick.SidekickRecord;
import com.gdx.kaps.renderer.Animated;
import com.gdx.kaps.renderer.Zone;
import com.gdx.kaps.time.Timer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gdx.kaps.MainScreen.*;
import static com.gdx.kaps.Utils.getRandomFrom;

public class Level implements Animated {
    // game
    private boolean paused = true;
    private int updateSpeed = 1_000_000_000;
    private final Timer updateTimer;
    private final Timer fallingCapsTimer;
    public final static int MIN_MATCH_RANGE = 4;
    private static List<Sidekick> sidekicks;
    private static int multiplier = 1;
    private static int score;
    // render
    private final Sprite starSprite = new Sprite(new Texture("android/assets/img/icons/star.png"));
    private final SoundStream stream = new SoundStream();
    private static final List<EffectAnim> effects = new ArrayList<>();
    private static Particles particles;
    // level
    private final Grid grid;
    private final Set<com.gdx.kaps.level.grid.Color> colors;
    // gelule
    private boolean canHold;
    private final List<Caps> fallingCaps = new ArrayList<>();
    private final List<ControllableGelule> gelules = new ArrayList<>();
    private final Gelule[] next = new Gelule[3];
    private Gelule hold;

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
        fallingCapsTimer = new Timer(updateSpeed);
        updateTimer = new Timer(updateSpeed);
        particles = new Particles(sidekicks);
        starSprite.flip(false, true);
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
        ).orElse(sidekicks().get(0));
    }

    private boolean isOver() {
        boolean defeat = gelules.stream()
                           .map(g -> !g.isAtValidEmplacement(grid))
                           .reduce((b1, b2) -> b1 || b2)
                           .orElse(false);
        boolean victory = grid.remainingGerms() <= 0;
        boolean noMoreAnims = !grid.hasPoppingCaps();
        return (defeat || victory) && noMoreAnims;
    }

    public boolean isPaused() {
        return paused;
    }

    // control

    public void moveGeluleLeft(ControllableGelule g) {
        if (!g.moveLeftIfPossible(grid)) stream.play("cant");
        g.updatePreview();
    }

    public void moveGeluleRight(ControllableGelule g) {
        if (!g.moveRightIfPossible(grid)) stream.play("cant");
        g.updatePreview();
    }

    public void dipGelule(ControllableGelule g) {
        if (!g.dipIfPossible(grid)) acceptGelule(g);
    }

    public void flipGelule(ControllableGelule g) {
        stream.play(g.flipIfPossible(grid) ? "flip" : "cant");
        g.updatePreview();
    }

    public void dropGelule(ControllableGelule g) {
        stream.play("drop");
        while (g.dipIfPossible(grid));
        acceptGelule(g);
        gelules.removeIf(ControllableGelule::isAccepted);
    }

    public void dipCaps(Caps caps) {
        caps.dipIfPossible(grid);
        if (!caps.canDip(grid)) {
            grid.accept(caps);
            stream.play("light_impact");
        }
    }

    public void moveGeluleLeft() {
        if (gelules.isEmpty()) return;
        moveGeluleLeft(gelules.get(0));
    }

    public void moveGeluleRight() {
        if (gelules.isEmpty()) return;
        moveGeluleRight(gelules.get(0));
    }

    public void dipGelule() {
        if (gelules.isEmpty()) return;
        dipGelule(gelules.get(0));
    }

    public void flipGelule() {
        if (gelules.isEmpty()) return;
        flipGelule(gelules.get(0));
    }

    public void dropGelule() {
        if (gelules.isEmpty()) return;
        dropGelule(gelules.get(0));
    }

    public void togglePause() {
        if (!paused) stream.play("pause");
        paused = !paused;
    }

    public void hold() {
        if (!canHold) return;
        var tmp = Optional.ofNullable(hold);
        hold = Gelule.copyColorOf(gelules.get(0), new Gelule(this));
        tmp.ifPresentOrElse(
          hold -> {
              gelules.add(ControllableGelule.copyColorOf(hold, gelules.get(0), grid));
              gelules.remove(0);
          },
          () -> {
              gelules.clear();
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
        if (!gelules.isEmpty() || isOver()) return;
        gelules.add(ControllableGelule.of(next[0].copy(), grid));

        updateNext();
        canHold = true;
        multiplier = 1;
    }

    private void acceptGelule(ControllableGelule gelule) {
        grid.accept(gelule);
        gelule.setAccepted();
        stream.play("impact");

        updateGrid();
    }

    private void triggerSidekicks() {
        // TODO: animation focus when sidekicks are triggered
        sidekicks.forEach(sdk -> sdk.triggerIfReady(this));
    }

    private void decreaseCooldowns() {
        sidekicks.forEach(Sidekick::decreaseCooldown);
        triggerSidekicks();
        grid.everyGermWithCooldown()
          .forEach(g -> {
              g.decreaseCooldown();
              g.triggerIfReady(this);
          });
    }

    private void speedUp() {
        updateSpeed *= 0.996;
        updateTimer.updateLimit(updateSpeed);
        updateTimer.reset();
        fallingCapsTimer.updateLimit(updateSpeed/2);
        fallingCapsTimer.reset();
    }

    public void addFallingCaps(Caps caps) {
        fallingCaps.add(caps);
    }

    // update
    @Override
    public void update() {
        if (paused) return;
        // FIXME: update on pause (récup remaining time)
        if (updateTimer.resetIfExceeds()) {
            gelules.forEach(this::dipGelule);
            gelules.removeIf(ControllableGelule::isAccepted);
        }
        if (fallingCapsTimer.resetIfExceeds()) {
            fallingCaps.forEach(this::dipCaps);
            fallingCaps.removeIf(c -> !c.canDip(grid));
            updateGrid();
        }

        if (gelules.isEmpty()) {
            triggerSidekicks();
            decreaseCooldowns();
            speedUp();
            spawnNewGelule();
        }

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

    public void updateGrid() {
        Matches matches;
        do {
            grid.dropAll(this);
            matches = grid.hitMatches()
                        .peek((color, set) -> {
                            var sound = "plop0";
                            if (!sidekickOfColor(color).map(Sidekick::hasCooldown).orElse(true)) {
                                particles.add(set);
                                // bonus particles
                                if (set.size() > MIN_MATCH_RANGE) {
                                    for (int i = 0; i < set.size() - MIN_MATCH_RANGE; i++) {
                                        getRandomFrom(set).ifPresent(particles::add);
                                    }
                                }
                            }
                            else if (set.size() > MIN_MATCH_RANGE) {
                                particles.add(set);
                                sidekickOfColor(color).ifPresent(Sidekick::decreaseCooldown);
                                sound = "match_five";
                            }
                            stream.play(sound);
                        });
            multiplier++;
        } while (!matches.isEmpty());
        gelules.forEach(ControllableGelule::updatePreview);
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
        tra25.drawShadedText("NEXT", dim.get(Zone.NEXT_BOX).x, dim.get(Zone.NEXT_BOX).y + dim.get(Zone.NEXT_BOX).height + 10);

        sra.drawRect(
          dim.get(Zone.HOLD_BOX),
          new Color(0.45f, 0.5f, 0.6f, 1)
        );
        tra25.drawShadedText("HOLD", dim.get(Zone.HOLD_BOX).x, dim.get(Zone.HOLD_BOX).y + dim.get(Zone.HOLD_BOX).height + 10);

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

        tra25.drawShadedText(score + "", dim.get(Zone.BOTTOM_PANEL));
        tra15.drawText( "SCORE:",
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
            tra25.drawShadedText(sdk.name(), 10, dim.gridMargin * 2 + n * (dim.gridMargin + panelHeight) + 10);

            // head & stars
            sdk.render(10, dim.gridMargin * 2 + n * (dim.gridMargin + panelHeight) + 45, 75, 75);
            for (int s = 0; s < 3; s++) {
                // TODO: draw real stars (sprites ?)
                spra.render(
                  starSprite,
                  20 * (s + 1),
                  dim.gridMargin * 2 + n * (dim.gridMargin + panelHeight) + 55 + 75,
                  15, 15,
                  s < sdk.stats().stars() ? 1 : 0.4f
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
            for (int i = 0; i < sdk.description().length; i++) {
                tra15.drawText(sdk.description()[i], 10, dim.gridMargin * 2 + n * (dim.gridMargin + panelHeight) + 55 + 75 + 25 + i * 20);
            }

            tra25.drawShadedText(
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
        gelules.forEach(Gelule::render);
        fallingCaps.forEach(Caps::render);

        next[0].render(dim.get(Zone.NEXT_GELULE));
        Optional.ofNullable(hold).ifPresent(hold -> hold.render(dim.get(Zone.HOLD_GELULE)));
        renderPauseScreen();

        renderEffects();
        particles.render();
        renderSidekicks();
    }
}
