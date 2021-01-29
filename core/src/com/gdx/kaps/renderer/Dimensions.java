package com.gdx.kaps.renderer;

import com.badlogic.gdx.math.Rectangle;
import com.gdx.kaps.level.grid.Grid;
import com.gdx.kaps.level.grid.GridObject;

import java.util.EnumMap;

public class Dimensions {
    private final EnumMap<Zone, Rectangle> map = new EnumMap<>(Zone.class);
    private final Rectangle[][] tiles;

    public final float tileSize;
    public final float sidePadding;
    public final float gridMargin;
    public final float boxPadding;
    public final float sidekickPanelHeight;

    public Dimensions(Grid grid, int windowWidth, int windowHeight) {
        map.put(Zone.WINDOW, new Rectangle(0, 0, windowWidth, windowHeight));
        gridMargin = map.get(Zone.WINDOW).width / 20;

        // grid
        map.put(Zone.GRID_PANEL, new Rectangle(0, 0, map.get(Zone.WINDOW).width*2/3, map.get(Zone.WINDOW).height));
        map.put(Zone.GRID, new Rectangle(
          gridMargin, gridMargin,
          map.get(Zone.GRID_PANEL).width - 2 * gridMargin,
          ((map.get(Zone.GRID_PANEL).width - 2 * gridMargin) / grid.width()) * grid.height()
        ));
        map.get(Zone.GRID_PANEL).height = map.get(Zone.GRID).height + 2* gridMargin;

        // tiles
        tileSize = map.get(Zone.GRID).width / grid.width();
        tiles = new Rectangle[grid.width()][grid.height()];
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                tiles[x][y] = new Rectangle(
                    get(Zone.GRID).x + x * tileSize,
                    get(Zone.GRID).y + ((tiles[0].length -1) - y) * tileSize,
                    tileSize,
                    tileSize
                );
            }
        }

        // side panel
        map.put(Zone.SIDE_PANEL, new Rectangle(map.get(Zone.GRID_PANEL).width, 0, map.get(Zone.WINDOW).width - map.get(Zone.GRID_PANEL).width, map.get(Zone.WINDOW).height));
        map.put(Zone.BOTTOM_PANEL, new Rectangle(0, map.get(Zone.WINDOW).height - 100, map.get(Zone.WINDOW).width, 100));
        sidePadding = map.get(Zone.SIDE_PANEL).width / 10;

        map.put(Zone.NEXT_BOX, new Rectangle(map.get(Zone.SIDE_PANEL).x + sidePadding, gridMargin, map.get(Zone.SIDE_PANEL).width - 2 * sidePadding, map.get(Zone.SIDE_PANEL).width - 2 * sidePadding));
        map.put(Zone.HOLD_BOX, new Rectangle(map.get(Zone.SIDE_PANEL).x + sidePadding, gridMargin * 3 + map.get(Zone.NEXT_BOX).height, map.get(Zone.SIDE_PANEL).width - 2 * sidePadding, map.get(Zone.SIDE_PANEL).width - 2 * sidePadding));
        boxPadding = map.get(Zone.NEXT_BOX).width / 10;

        map.put(Zone.NEXT_GELULE, new Rectangle(map.get(Zone.NEXT_BOX).x + boxPadding, map.get(Zone.NEXT_BOX).y + boxPadding, map.get(Zone.NEXT_BOX).width - 2 * boxPadding, (map.get(Zone.NEXT_BOX).width - 2 * boxPadding) / 2));
        map.get(Zone.NEXT_GELULE).y = map.get(Zone.NEXT_BOX).y + map.get(Zone.NEXT_BOX).height/2 - map.get(Zone.NEXT_GELULE).height/2;
        map.put(Zone.HOLD_GELULE, new Rectangle(map.get(Zone.HOLD_BOX).x + boxPadding, map.get(Zone.HOLD_BOX).y + map.get(Zone.HOLD_BOX).height/2 - map.get(Zone.NEXT_GELULE).height/2, map.get(Zone.HOLD_BOX).width - 2 * boxPadding, (map.get(Zone.HOLD_BOX).width - 2 * boxPadding) / 2));

        sidekickPanelHeight = map.get(Zone.NEXT_BOX).height / 2;
    }

    public Rectangle get(Zone zone) {
        return map.get(zone);
    }

    public Rectangle getTile(GridObject o) {
        return getTile(o.x(), o.y());
    }

    public Rectangle getTile(int x, int y) {
        return tiles[x][y];
    }
}
