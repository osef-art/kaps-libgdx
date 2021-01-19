package com.gdx.kaps.level.grid.caps;

import static com.gdx.kaps.MainScreen.dim;

public class PreviewGelule extends Gelule {
    public PreviewGelule(Gelule gelule) {
        super(gelule);
    }

    public void render() {
        forEach(caps -> caps.render(
          dim.gridMargin + caps.x() * dim.tile.height,
          dim.topTile(caps.y()),
          dim.tile.width,
          dim.tile.height,
          0.5f
        ));
    }
}
