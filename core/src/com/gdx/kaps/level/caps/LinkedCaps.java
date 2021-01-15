package com.gdx.kaps.level.caps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gdx.kaps.level.Grid;
import com.gdx.kaps.level.Level;

import java.util.Optional;

import static com.gdx.kaps.MainScreen.batch;
import static com.gdx.kaps.MainScreen.dim;
import static java.util.Objects.requireNonNull;

public class LinkedCaps extends Caps {
    private final Sprite mark = new Sprite(new Texture("img/9/caps/pop_0.png"));
    private Sprite sprite;
    private LinkedCaps linked;
    private Look look;

    public LinkedCaps(int x, int y, Look look, Level lvl) {
        this(x, y, look, Color.random(requireNonNull(lvl).colors()), lvl.grid());
    }

    LinkedCaps(int x, int y, Look look, Color color, Grid grid) {
        super(x, y, color, grid);
        requireNonNull(look);
        this.look = look;
        updateTexture();
    }

    public LinkedCaps(LinkedCaps caps) {
        super(caps);
        linked = new LinkedCaps(
          caps.linked.x(),
          caps.linked.y(),
          caps.linked.look,
          caps.linked.color(),
          caps.linked.grid
        );
        look = caps.look;
        sprite = caps.sprite;
    }

    // getters

    @Override
    public Optional<Caps> linked() {
        return Optional.ofNullable(linked);
    }

    @Override
    public Caps unlinked() {
        return new Caps(x(), y(), color(), grid);
    }

    LinkedCaps copy() {
        return new LinkedCaps(this);
    }

    LinkedCaps shifted(Look look) {
        requireNonNull(look);
        var copy = copy();
        copy.position.add(look.vector());
        return copy;
    }

    public Look look() {
        return look;
    }

    // movement

    void flip() {
        look = look.flipped();
        updateTexture();
    }

    // update

    private void updateTexture() {
        sprite = new Sprite(new Texture("img/" + color().id() + "/caps/" + look + ".png"));
        sprite.flip(false, true);
    }

    void updateLinked() {
        linked.look = look.opposite();
        linked.position.set(position);
        linked.position.add(linked.look.vector());
        linked.updateTexture();
    }

    void linkTo(LinkedCaps caps) {
        this.linked = caps;
        caps.linked = this;
        updateLinked();
    }


    @Override
    public void render() {
        render(position.x(), position.y());
    }

    @Override
    public void render(int x, int y) {
        render(x, y, false);
    }

    public void render(int x, int y, boolean main) {
        batch.begin();
        batch.draw(
          sprite,
          dim.boardMargin + x * dim.tile.height,
          dim.topTile(y),
          dim.tile.width,
          dim.tile.height
        );
        if (main) {
            batch.draw(
              mark,
              dim.boardMargin + x * dim.tile.height + dim.tile.width / 4,
              dim.topTile(y) + dim.tile.height / 4,
              dim.tile.width / 2,
              dim.tile.height / 2
            );
        }
        batch.end();
    }
}
