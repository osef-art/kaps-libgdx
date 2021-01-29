package com.gdx.kaps.level.grid.caps;

import static com.gdx.kaps.MainScreen.dim;

public class PreviewGelule extends Gelule {
    public PreviewGelule(Gelule gelule) {
        super(gelule);
    }

    public void render() {
        forEach(caps -> caps.render(dim.getTile(caps), 0.5f));
    }
}
