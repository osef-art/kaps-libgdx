package com.gdx.kaps.level.grid.caps;

import com.gdx.kaps.level.grid.Position;

enum Look {
    LEFT("left", new Position(-1, 0)),
    UP("up", new Position(0, 1)),
    RIGHT("right", new Position(1, 0)),
    DOWN("down", new Position(0, -1)),
    NONE("unlinked", new Position(0, 0))
    ;

    private final Position vector;
    private final String name;

    Look(String name, Position position) {
        vector = position;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    Position vector() {
        return vector;
    }

    Look flipped() {
        switch (this) {
            case LEFT:
                return UP;
            case UP:
                return RIGHT;
            case RIGHT:
                return DOWN;
            case DOWN:
                return LEFT;
        }
        return NONE;
    }

    Look opposite() {
        switch (this) {
            case LEFT:
                return RIGHT;
            case UP:
                return DOWN;
            case RIGHT:
                return LEFT;
            case DOWN:
                return UP;
        }
        return NONE;
    }
}
