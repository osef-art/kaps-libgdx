package com.gdx.kaps.level.grid;

import com.gdx.kaps.level.Level;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class LinkedCaps extends Caps {
    private LinkedCaps linked;
    private Look look;

    LinkedCaps(int x, int y, Look look, Level lvl) {
        this(x, y, look, Color.random(requireNonNull(lvl).colors()));
    }

    LinkedCaps(int x, int y, Look look, Color color) {
        super(x, y, color);
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
        return new Caps(x(), y(), color());
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

    // update

    void updateSprite() {
        updateSprite(
          "android/assets/img/" + color().id() +
            "/caps/" + look + ".png"
        );
    }

    void updateLinked() {
        requireNonNull(linked);
        linked.look = look.opposite();
        linked.position.set(position);
        linked.move(linked.look);
        linked.updateSprite();
    }

    void linkTo(LinkedCaps caps) {
        this.linked = caps;
        caps.linked = this;
        updateLinked();
    }
}
