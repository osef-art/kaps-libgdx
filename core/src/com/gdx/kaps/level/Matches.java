package com.gdx.kaps.level;

import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.GridObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Matches {
    private final HashMap<Color, Set<GridObject>> matches = new HashMap<>();

    public Matches(Set<GridObject> objects) {
        objects.forEach(this::add);
    }

    private void add(GridObject o) {
        if (!matches.containsKey(o.color())) {
            matches.put(o.color(), new HashSet<>());
        }
        matches.get(o.color()).add(o);
    }

    public boolean isEmpty() {
        return matches.isEmpty();
    }

    public Set<GridObject> rangeOf(Color color) {
        return matches.getOrDefault(color, new HashSet<>());
    }

    public void forEach(BiConsumer<? super Color, ? super Set<GridObject>> action) {
        matches.forEach(action);
    }

    public Matches peek(BiConsumer<? super Color, ? super Set<GridObject>> action) {
        forEach(action);
        return this;
    }

    public Matches peekObjects(Consumer<? super GridObject> action) {
        peek((c, set) -> set.forEach(action));
        return this;
    }
}
