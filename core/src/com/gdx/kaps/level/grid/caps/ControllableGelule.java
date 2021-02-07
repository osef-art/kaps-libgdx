package com.gdx.kaps.level.grid.caps;

import com.gdx.kaps.level.grid.Grid;

import static com.gdx.kaps.MainScreen.dim;

public class ControllableGelule extends Gelule {
    private final Grid grid;
    private Gelule preview;

    public ControllableGelule(Gelule gelule, Grid grid) {
        super(gelule);
        this.grid = grid;
        updatePreview();
    }

    private ControllableGelule(Gelule color, Gelule pos, Grid grid) {
        super(color, pos);
        this.grid = grid;
        updatePreview();
    }

    public static ControllableGelule copyColorOf(Gelule color, Gelule pos, Grid grid) {
        return new ControllableGelule(color, pos, grid);
    }

    public static ControllableGelule of(Gelule gelule, Grid grid) {
        return new ControllableGelule(gelule, grid);
    }

    public void updatePreview() {
        preview = new Gelule(this);
        while (preview.dipIfPossible(grid));
    }

    @Override
    public void render() {
        preview.forEach(c -> c.render(dim.getTile(c), 0.5f));
        super.render();
    }
}
