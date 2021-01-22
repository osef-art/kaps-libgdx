package com.gdx.kaps.level.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.gdx.kaps.Utils;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.caps.Gelule;
import com.gdx.kaps.level.grid.germ.Germ;
import com.gdx.kaps.renderer.Dimensions;
import com.gdx.kaps.renderer.Renderable;
import com.gdx.kaps.renderer.Zone;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.gdx.kaps.MainScreen.dim;
import static com.gdx.kaps.MainScreen.sra;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.*;

public class Grid implements Renderable {
    static class Column {
        private final GridObject[] tiles;
        Column(int size) {
            tiles = new GridObject[size];
        }
    }
    private final Column[] columns;

    // init

    public Grid(int width, int height) {
        if (width < 2 || height < 2) {
            throw new IllegalArgumentException("Invalid grid size: " + width + "x" + height);
        }

        columns = new Column[width];

        for (int i = 0; i < width; i++) {
            columns[i] = new Column(height);
        }
    }

    public static Grid parseLevel(Path path, Set<com.gdx.kaps.level.grid.Color> colors) throws IOException {
        BufferedReader reader = Files.newBufferedReader(path);
        var charGrid = new ArrayList<List<Character>>();
        String line;

        while ((line = reader.readLine()) != null) {
            charGrid.add(0,
              Arrays.stream(line.split(""))
                .map(sym -> sym.charAt(0))
                .collect(toList())
            );
        }

        if (charGrid.size() == 0 || !(charGrid.stream().allMatch(l -> l.size() == charGrid.get(0).size())))
            throw new IllegalStateException("Invalid file: " + path);

        var grid = new Grid(charGrid.get(0).size(), charGrid.size());
        dim = new Dimensions(grid, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        do {
            for (int y = 0; y < charGrid.size(); y++) {
                for (int x = 0; x < charGrid.get(y).size(); x++) {
                    grid.set(x, y, Germ.of(x, y, charGrid.get(y).get(x), colors));
                }
            }
        } while (grid.containsMatches());

        return grid;
    }

    // getters

    /**
     * @return the grid width.
     */
    public int width() {
        return columns.length;
    }

    /**
     * @return the grid height.
     */
    public int height() {
        return columns[0].tiles.length;
    }

    /**
     * @return an {@link Optional<GridObject>} containing the element occupying the position (x, y).
     * The optional is empty if there's no element.
     */
    public Optional<GridObject> get(int x, int y) {
        return Optional.ofNullable(columns[x].tiles[y]);
    }

    public Optional<GridObject> pickRandomObject() {
        return Utils.getRandomFrom(everyObjectInGrid().collect(toList()));
    }

    public int remainingGerms() {
        return (int) everyObjectInGrid()
          .filter(GridObject::isGerm)
          .count();
    }

    // predicates

    /**
     * @return true if the position (x, y) is accessible in the grid,
     * false if it exceeds dimensions.
     * @param x the column number
     * @param y the row number
     */
    public boolean isInGrid(int x, int y) {
        return 0 <= x && x < width() &&
                 0 <= y && y < height();
    }

    /**
     * @return true if an element is already set in the grid at position (x, y),
     * false if not.
     * @param x the column number
     * @param y the row number
     */
    public boolean collidesPile(int x, int y) {
        return get(x, y).isPresent();
    }

    /**
     * @return true if a obj can dip, meaning the obj and its hypothetical linked obj have
     * a free tile below them (at their y index minus 1 in the grid).
     * Returns false if not.
     * @param obj the obj to check
     */
    private boolean canDip(GridObject obj) {
        boolean canDip = obj.canDip(this);
        canDip &= obj.linked().map(o -> o.canDip(this)).orElse(true);
        return canDip;
    }

    private boolean containsMatches() {
        return !matchingObjects().isEmpty();
    }

    // transactions

    /**
     * Applies an operation on a obj, and on its linked obj if it has one.
     * @param obj the obj on which apply th operation
     * @param action the operation, a {@link Consumer<GridObject>}
     */
    private void doOnGridObjectAndLinkedIfExists(GridObject obj, Consumer<GridObject> action) {
        action.accept(obj);
        obj.linked().ifPresent(action);
    }

    /**
     * Sets the obj in the grid. Its position depends on the obj position.
     * @param obj the obj to put into the grid
     */
    private void set(GridObject obj) {
        doOnGridObjectAndLinkedIfExists(obj, c -> set(c.x(), c.y(), c));
    }

    /**
     * Sets in the gris the object {@code obj} at position (x, y)
     * @param x the column number
     * @param y the row number
     * @param obj the obj to set
     */
    private void set(int x, int y, GridObject obj) {
        columns[x].tiles[y] = obj;
    }

    /**
     * Sets the grid element at coordinates (x, y) to null, no matter what it is.
     * @param x the row number
     * @param y the column number
     */
    private void remove(int x, int y) {
        set(x, y, null);
    }

    /**
     * Removes a whole obj from the grid, meaning both parts of it if there are two.
     * @param obj the obj to remove from grid
     */
    private void remove(GridObject obj) {
        doOnGridObjectAndLinkedIfExists(obj, c -> remove(c.x(), c.y()));
    }

    private void hit(GridObject obj) {
        get(obj.x(), obj.y()).ifPresent(o -> {
            o.hit();
            if (o.isDestroyed()) pop(o);
        });
    }

    /**
     * Pops a obj depending on its inner indexes.
     * @param obj the obj to pop.
     */
    private void pop(GridObject obj) {
        get(obj.x(), obj.y()).flatMap(GridObject::linked).ifPresent(this::unlink);
        remove(obj.x(), obj.y());
    }

    /**
     * Sets grid element located at (x, y) to an unlinked GridObject.
     * @param obj the element to unlink
     */
    private void unlink(GridObject obj) {
        set(obj.x(), obj.y(), obj.unlinked());
    }

    /**
     * Dips a obj from the grid by translating it to an index below.
     * @param obj the obj to dip
     */
    private void dip(GridObject obj) {
        doOnGridObjectAndLinkedIfExists(obj, c -> {
            remove(c.x(), c.y());
            c.dipIfPossible(this);
            set(c);
        });
    }

    /**
     * Dips the provided obj and updates its position into the grid if needed
     * @param obj the obj to dip
     * @return true if the obj was dipped, false if its positon is unchanged
     */
    private boolean dipIfPossible(GridObject obj) {
        requireNonNull(obj);

        remove(obj);
        if (!canDip(obj)) {
            set(obj);
            return false;
        }
        dip(obj);
        return true;
    }

    /**
     * Sets the content of a gelule into the grid using its indexes
     * @param gelule the gelule to add to the grid
     */
    public void accept(Gelule gelule) {
        requireNonNull(gelule);
        gelule.forEach(this::set);
    }

    // full grid operations

    /**
     * Applies gravity on every obj and dips them all until there is no more obj to dip.
     */
    public void dropAll() {
        // TODO: drop only linked obj. unlinked will be controllable
        //  until they hit the floor/another obj. Also, drop unlinked below falling linked.
        while (
          everyObjectInGrid()
            .map(this::dipIfPossible)
            .reduce((bool, bool2) -> bool || bool2)
            .orElse(false)
        );
    }


    public Set<GridObject> deleteMatches() {
        var matches = matchingObjects();
        matches.forEach(this::hit);
        return matches;
    }

    private Set<GridObject> matchingObjects() {
        return everyObjectInGrid()
                 .flatMap(obj -> {
                     var toDelete = new HashSet<GridObject>();
                     if (obj.x() >= Level.MIN_MATCH_RANGE - 1) {
                         toDelete.addAll(matchingObjectsFrom(n -> get(obj.x() - n, obj.y())));
                     }
                     if (obj.y() >= Level.MIN_MATCH_RANGE - 1) {
                         toDelete.addAll(matchingObjectsFrom(n -> get(obj.x(), obj.y() - n)));
                     }
                     return toDelete.stream();
                 })
                 .collect(toUnmodifiableSet());
    }

    /**
     * Takes a list of {@link Optional} obj, checks if they are all there and of the same color.
     * If yes, adds them to {@code toDelete}.
     * @return a {@link Collection} of the obj to delete.
     * The collection is empty if the obj don't match.
     */
    private List<GridObject> matchingObjectsFrom(IntFunction<Optional<GridObject>> collector) {
        // IMPL: cleaner way to do this ?
        var colors = IntStream.range(0, Level.MIN_MATCH_RANGE)
                       .mapToObj(collector)
                      .map(opt -> opt.map(GridObject::color).orElse(null))
                      .filter(Objects::nonNull)
                      .collect(toUnmodifiableList());

        if (colors.size() >= Level.MIN_MATCH_RANGE && colors.stream().allMatch(colors.get(0)::equals)) {
            return IntStream.range(0, Level.MIN_MATCH_RANGE)
                     .mapToObj(collector)
                     .map(Optional::get)
                     .collect(toUnmodifiableList());
        }
        return new ArrayList<>();
    }

    /**
     * @return a flatmap collecting every obj of each {@link Column} of the grid.
     */
    private Stream<GridObject> everyObjectInGrid() {
        return Arrays.stream(columns)
          .flatMap(col -> Arrays.stream(col.tiles))
          .filter(Objects::nonNull);
    }

    @Override
    public void update() {
        everyObjectInGrid().forEach(Renderable::update);
    }

    @Override
    public void render() {
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                float shift = (x + y) %2 == 0 ? 0.0175f : 0;
                Color color = new Color(0.45f + shift, 0.5f + shift, 0.6f + shift, 1);

                sra.drawRect(
                  dim.gridMargin + x * dim.get(Zone.TILE).width,
                  dim.gridMargin + y * dim.get(Zone.TILE).height,
                  dim.get(Zone.TILE).width,
                  dim.get(Zone.TILE).height,
                  color
                );
            }
        }
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                int finalX = x;
                int finalY = y;
                get(x, y).ifPresent(obj -> obj.render(finalX, finalY));
            }
        }
    }

    @Override
    public void render(float x, float y, float width, float height) {

    }
}
