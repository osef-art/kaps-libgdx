package com.mygdx.stickrush;

import com.badlogic.gdx.graphics.Texture;

import static com.mygdx.stickrush.Game.dim;

public class Tile extends ImgDrawable {
    private final Position position;  // Impl: must be in grid ? i mean, why
                                      //  must a tile know its position in a grid ?
    private boolean popping;
    Token content;

    public Tile(int x, int y) {
        this(x, y, Token.random());
    }
    public Tile(int x, int y, Token token) {
        super(dim.tile, token.texture());
        position = new Position(x, y);
        popping = false;
        content = token;
    }

    public Texture texture() {
        return content.texture();
    }

    public boolean hasSameTokenThan(Tile tile) {
        return tile != null && content == tile.content;
    }

    public boolean isPopping() {
        return popping;
    }

    public boolean isEmpty() {
        return null == content;
    }

    public void pop() {
        content = null;
    }

    public void planPopping() {
        popping = true;
    }

    public void fill(Token token) {
        content = token;
        popping = false;
    }

    public void swap(Tile tile) {
        Token prev = tile.content;
        tile.content = content;
        content = prev;
    }

    @Override
    public void addTextures() {

    }

    @Override
    public void render() {
        batch.setProjectionMatrix(Game.camera.combined);

        if (content != null) {
            batch.begin();

            /* (follow cursor)
            if (selected) {
                batch.draw(
                  texture(),
                  cursor.getX() - dim.token.width / 2,
                  cursor.getY() - dim.token.height / 2,
                  dim.token.width,
                  dim.token.height
                );
            }
            */
            batch.draw(
              texture(),
              dim.grid.x + dim.tokenPadding + position.getX() * dim.tile.width,
              dim.grid.y + dim.tokenPadding + position.getY() * dim.tile.height,
              dim.token.width,
              dim.token.height
            );

            batch.end();
        }
    }
}
