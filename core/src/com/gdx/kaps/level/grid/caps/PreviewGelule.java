package com.gdx.kaps.level.grid.caps;

import com.gdx.kaps.renderer.Zone;

import static com.gdx.kaps.MainScreen.dim;

public class PreviewGelule extends Gelule {
    public PreviewGelule(Gelule gelule) {
        super(gelule);
    }

    public void render() {
        forEach(caps -> caps.render(
          dim.gridMargin + caps.x() * dim.get(Zone.TILE).height,
          dim.topTile(caps.y()),
          dim.get(Zone.TILE).width,
          dim.get(Zone.TILE).height,
          0.5f
        ));
    }
}
