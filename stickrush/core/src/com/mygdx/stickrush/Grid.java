package com.mygdx.stickrush;

import com.badlogic.gdx.graphics.Color;
import com.mygdx.stickrush.renderer.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.mygdx.stickrush.Game.dim;
import static com.mygdx.stickrush.Game.level;
import static java.util.Objects.requireNonNull;

public class Grid extends ShapeDrawable {
    private static class Column implements Iterable<Tile> {
        private final static int OVERFLOW = 5;
        private final List<Tile> list;
        public Column(int height, int colIndex) {
            list = new ArrayList<>(height + OVERFLOW);
            for (int i = 0; i < height + OVERFLOW; i++) {
                list.add(new Tile(colIndex, i));
            }
        }

        public Tile get(int y) {
            return list.get(y);
        }

        public int height() {
            return list.size() - 5;
        }

        public void set(int y, Token token) {
            get(y).fill(token);
        }

        public void dipAll() {
            for (int y = 1; y < list.size(); y++) {
                if (get(y-1).isEmpty()) {
                    set(y-1, get(y).content);
                    set(y, null);
                }
                if (y == list.size() - 1 && get(y).isEmpty()) {
                    set(y, Token.random());
                }
            }
        }

        public boolean isFilled() {
            for (int y = 0; y < height(); y++) {
                if (get(y).isEmpty()) return false;
            }
            return true;
        }

        @Override
        public Iterator<Tile> iterator() {
            return list.iterator();
        }
    }
    private final List<Column> columns;
    private Position selected;

    public Grid(int width, int height) {
        columns = new ArrayList<>(width);
        for (int i = 0; i < height; i++) {
            columns.add(new Column(height, i));
        }
        findTilesToPop();
        addTextures();
    }

    public boolean isFilled() {
        for (Column column : columns) {
            if (!column.isFilled()) return false;
        }
        return true;
    }

    private boolean findTilesToPop() {
        boolean found = false;

        for (int x = 0; x < columns.size(); x++) {
            for (int y = 0; y < columns.get(x).height(); y++) {
                if ((0 < x && x < columns.size() - 1) &&
                      get(x, y).hasSameTokenThan(left(x,y)) &&
                      get(x, y).hasSameTokenThan(right(x,y))) {
                    for (int i = -1; i <= 1; i++) planPopping(x + i, y);
                    found = true;
                }
                else if ((0 < y && y < columns.get(x).height() - 1) &&
                           get(x, y).hasSameTokenThan(top(x,y)) &&
                           get(x, y).hasSameTokenThan(bottom(x,y))) {
                    for (int i = -1; i <= 1; i++) planPopping(x, y + i);
                    found = true;
                }
            }
        }
        return found;
    }

    private boolean isNextToSelected(int x, int y) {
        return
          selected.equals(new Position(x, y+1)) ||
            selected.equals(new Position(x, y-1)) ||
            selected.equals(new Position(x+1, y)) ||
            selected.equals(new Position(x-1, y))
          ;
    }

    public boolean hasSelection() {
        return selected != null;
    }

    public Tile get(int x, int y) {
        return columns.get(x).get(y);
    }

    private Tile left(int x, int y) {
        return get(x-1, y);
    }

    private Tile right(int x, int y) {
        return get(x+1, y);
    }

    private Tile bottom(int x, int y) {
        return get(x, y-1);
    }

    private Tile top(int x, int y) {
        return get(x, y+1);
    }

    public void unselect() {
        if (selected != null) {
            selected = null;
        }
    }

    public void popAll() {
        if (!findTilesToPop()) return;
        for (int x = 0; x < columns.size(); x++) {
            for (int y = 0; y < columns.get(x).height(); y++) {
                if (get(x, y).isPopping()) {
                    get(x, y).pop();
                    level.incerementScore();
                }
            }
        }
    }

    public void dipAll() {
        for (Column column : columns) {
            column.dipAll();
        }
    }

    private void planPopping(int x, int y) {
        get(x, y).planPopping();
    }

    public void select(int x, int y) {
        if (selected == null || !isNextToSelected(x, y)) {
            selected = new Position(x, y);
        }
    }

    private void swap(int x, int y) {
        requireNonNull(selected);
        Tile selectedTile = get(selected.getX(), selected.getY());
        selectedTile.swap(get(selected.getX() + x, selected.getY() + y));

        if (!findTilesToPop()) {
            selectedTile.swap(get(selected.getX() + x, selected.getY() + y));
        }
        unselect();
    }
    public void swapLeft() {
        swap(-1, 0);
    }
    public void swapRight() {
        swap(1, 0);
    }
    public void swapDown() {
        swap(0,-1);
    }
    public void swapUp() {
        swap(0, 1);
    }


    @Override
    public void addTextures() {
        for (int x = 0; x < columns.size(); x++) {
            for (int y = 0; y < columns.get(x).height(); y++) {
                float shade = get(x, y).isPopping() ? 0.2f : (x+y) %2 * 0.05f;

                sra.add(new Rectangle(
                  dim.tile(x, y),
                  new Color(shade + 0.4f, shade + 0.3f, shade + 0.6f, 1)
                ));
            }
        }
    }

    @Override
    public void render() {
        sra.drawAll();

        for (Column col : columns) {
            for (int y = 0; y < col.height(); y++) {
                col.get(y).render();
            }
        }
        if (selected != null) {
            for (int i = 0; i <= 2; i++) {
                sra.drawOutlineRect(
                  dim.tile(selected.getX(), selected.getY(), -i),
                  new Color(1, 1, 1, 1)
                );
                get(selected.getX(), selected.getY()).render();
            }
        }
    }
}
