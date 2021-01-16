package com.gdx.kaps;

import com.badlogic.gdx.math.Rectangle;
import com.gdx.kaps.level.Level;

import static com.gdx.kaps.MainScreen.dim;

public class Dimensions {
    private static int GRID_HEIGHT;
    public final float sidePadding;
    public final float gridMargin;
    public final float boxPadding;
    public final Rectangle window;
    public final Rectangle grid;
    public final Rectangle tile;
    public final Rectangle bottomPanel;
    public final Rectangle gridPanel;
    public final Rectangle sidePanel;
    public final Rectangle nextBox;
    public final Rectangle holdBox;
    public final Rectangle nextGelule;
    public final Rectangle holdGelule;

    public Dimensions(Level level, int windowWidth, int windowHeight) {
        window = new Rectangle(0, 0, windowWidth, windowHeight);
        gridMargin = window.width / 20;
        GRID_HEIGHT = level.gridHeight();

        gridPanel = new Rectangle(0, 0, window.width*2/3, window.height);
        grid = new Rectangle(gridMargin, gridMargin, gridPanel.width - 2 * gridMargin, window.height);
        tile = new Rectangle(gridMargin, gridMargin, grid.width / level.gridWidth(), grid.width / level.gridWidth());
        grid.height = tile.height * GRID_HEIGHT;
        gridPanel.height = grid.height + 2* gridMargin;

        sidePanel = new Rectangle(gridPanel.width, 0, window.width - gridPanel.width, gridPanel.height);
        bottomPanel = new Rectangle(0, gridPanel.height + gridMargin, window.width, window.height - gridPanel.height - gridMargin);
        sidePadding = sidePanel.width / 10;

        nextBox = new Rectangle(sidePanel.x + sidePadding, gridMargin, sidePanel.width - 2 * sidePadding, sidePanel.width - 2 * sidePadding);
        holdBox = new Rectangle(sidePanel.x + sidePadding, gridMargin * 4 + nextBox.height, sidePanel.width - 2 * sidePadding, sidePanel.width - 2 * sidePadding);
        boxPadding = nextBox.width / 10;

        nextGelule = new Rectangle(nextBox.x + boxPadding, nextBox.y + boxPadding, nextBox.width - 2 * boxPadding, (nextBox.width - 2 * boxPadding) / 2);
        nextGelule.y = nextBox.y + nextBox.height/2 - nextGelule.height/2;
        holdGelule = new Rectangle(holdBox.x + boxPadding, holdBox.y + holdBox.height/2 - nextGelule.height/2, holdBox.width - 2 * boxPadding, (holdBox.width - 2 * boxPadding) / 2);
    }

    public float halfTile() {
        return tile.width/2;
    }

    public float sidePanelWithPadding() {
        return sidePanel.x + sidePadding;
    }

    public float topTile(int y) {
        return dim.gridMargin + ((GRID_HEIGHT -1) - y) * dim.tile.width;
    }
}
