package com.gdx.kaps.renderer;

import com.badlogic.gdx.math.Rectangle;
import com.gdx.kaps.level.grid.Grid;

import java.util.EnumMap;

import static com.gdx.kaps.MainScreen.dim;

public class Dimensions {
    private final EnumMap<Zone, Rectangle> map = new EnumMap<>(Zone.class);

    private final int GRID_HEIGHT;
    public final float sidePadding;
    public final float gridMargin;
    public final float boxPadding;

    public Dimensions(Grid grd, int windowWidth, int windowHeight) {
        map.put(Zone.WINDOW, new Rectangle(0, 0, windowWidth, windowHeight));
        gridMargin = map.get(Zone.WINDOW).width / 20;
        GRID_HEIGHT = grd.height();

        map.put(Zone.GRID_PANEL, new Rectangle(0, 0, map.get(Zone.WINDOW).width*2/3, map.get(Zone.WINDOW).height));
        map.put(Zone.GRID, new Rectangle(gridMargin, gridMargin, map.get(Zone.GRID_PANEL).width - 2 * gridMargin, map.get(Zone.WINDOW).height));
        map.put(Zone.TILE, new Rectangle(gridMargin, gridMargin, map.get(Zone.GRID).width / grd.width(), map.get(Zone.GRID).width / grd.width()));
        map.get(Zone.GRID).height = map.get(Zone.TILE).height * GRID_HEIGHT;
        map.get(Zone.GRID_PANEL).height = map.get(Zone.GRID).height + 2* gridMargin;

        map.put(Zone.SIDE_PANEL, new Rectangle(map.get(Zone.GRID_PANEL).width, 0, map.get(Zone.WINDOW).width - map.get(Zone.GRID_PANEL).width, map.get(Zone.WINDOW).height));
        map.put(Zone.BOTTOM_PANEL, new Rectangle(0, map.get(Zone.WINDOW).height - 100, map.get(Zone.WINDOW).width, 100));
        sidePadding = map.get(Zone.SIDE_PANEL).width / 10;

        map.put(Zone.NEXT_BOX, new Rectangle(map.get(Zone.SIDE_PANEL).x + sidePadding, gridMargin, map.get(Zone.SIDE_PANEL).width - 2 * sidePadding, map.get(Zone.SIDE_PANEL).width - 2 * sidePadding));
        map.put(Zone.HOLD_BOX, new Rectangle(map.get(Zone.SIDE_PANEL).x + sidePadding, gridMargin * 3 + map.get(Zone.NEXT_BOX).height, map.get(Zone.SIDE_PANEL).width - 2 * sidePadding, map.get(Zone.SIDE_PANEL).width - 2 * sidePadding));
        boxPadding = map.get(Zone.NEXT_BOX).width / 10;

        map.put(Zone.NEXT_GELULE, new Rectangle(map.get(Zone.NEXT_BOX).x + boxPadding, map.get(Zone.NEXT_BOX).y + boxPadding, map.get(Zone.NEXT_BOX).width - 2 * boxPadding, (map.get(Zone.NEXT_BOX).width - 2 * boxPadding) / 2));
        map.get(Zone.NEXT_GELULE).y = map.get(Zone.NEXT_BOX).y + map.get(Zone.NEXT_BOX).height/2 - map.get(Zone.NEXT_GELULE).height/2;
        map.put(Zone.HOLD_GELULE, new Rectangle(map.get(Zone.HOLD_BOX).x + boxPadding, map.get(Zone.HOLD_BOX).y + map.get(Zone.HOLD_BOX).height/2 - map.get(Zone.NEXT_GELULE).height/2, map.get(Zone.HOLD_BOX).width - 2 * boxPadding, (map.get(Zone.HOLD_BOX).width - 2 * boxPadding) / 2));

        map.put(Zone.SIDEKICK1_BOX, new Rectangle(map.get(Zone.SIDE_PANEL).x, gridMargin * 5 + map.get(Zone.NEXT_BOX).height * 2, map.get(Zone.SIDE_PANEL).width, map.get(Zone.NEXT_BOX).height / 2));
        map.put(Zone.SIDEKICK2_BOX, new Rectangle(map.get(Zone.SIDE_PANEL).x, gridMargin * 6 + map.get(Zone.NEXT_BOX).height * 2.5f, map.get(Zone.SIDE_PANEL).width, map.get(Zone.NEXT_BOX).height / 2));

        // TODO: both sidekicks heads. but first, an enum map. (+ handle n sidekicks ?)
    }

    public Rectangle get(Zone zone) {
        return map.get(zone);
    }

    public float topTile(int y) {
        return dim.gridMargin + ((GRID_HEIGHT -1) - y) * dim.map.get(Zone.TILE).width;
    }
}
