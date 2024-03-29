package com.gdx.kaps.level.grid.caps;

import com.gdx.kaps.level.grid.Color;
import com.gdx.kaps.level.grid.GridObject;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class LinkedCaps extends Caps {
    private LinkedCaps linked;
    private Look look;

    LinkedCaps(int x, int y, Look look, Color color) {
        this(x, y, look, color, Type.BASIC);
    }

    LinkedCaps(int x, int y, Look look, Color color, Type type) {
        super(x, y, color, type);
        requireNonNull(look);
        this.look = look;
        updateSprite();
    }

    LinkedCaps(LinkedCaps caps) {
        super(caps);
        linked = new LinkedCaps(
          caps.linked.x(),
          caps.linked.y(),
          caps.linked.look,
          caps.linked.color()
        );
        look = caps.look;
        updateSprite();
    }

    // getters

    @Override
    public Optional<GridObject> linked() {
        return Optional.of(requireNonNull(linked));
    }

    @Override
    public Caps unlinked() {
        return new Caps(this);
    }

    LinkedCaps copy() {
        return new LinkedCaps(this);
    }

    LinkedCaps shifted(Look look) {
        requireNonNull(look);
        var copy = copy();
        copy.move(look);
        return copy;
    }

    public Look look() {
        return look;
    }

    // movement

    void flip() {
        look = look.flipped();
        updateSprite();
    }

    @Override
    public void paint(Color color) {
        super.paint(color);
        updateSprite();
    }

    // update

    private void updateSprite() {
        updateSprite(
          "android/assets/img/" + color().id() +
            "/caps/" + type.path() + look + ".png"
        );
    }

    void updateLinked() {
        requireNonNull(linked);
        linked.look = look.opposite();
        linked.pos().set(pos());
        linked.move(linked.look);
        linked.updateSprite();
    }

    void linkTo(LinkedCaps caps) {
        this.linked = caps;
        caps.linked = this;
        updateLinked();
    }
}
