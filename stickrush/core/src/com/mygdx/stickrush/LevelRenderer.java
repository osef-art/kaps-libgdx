package com.mygdx.stickrush;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.stickrush.TextRendererAdaptor.TextBox.TextBoxBuilder;
import com.mygdx.stickrush.renderer.Rectangle;
import com.mygdx.stickrush.renderer.ShapeRendererAdaptor;

import static com.mygdx.stickrush.Game.camera;
import static com.mygdx.stickrush.Game.dim;

public class LevelRenderer implements AnimatedRenderer {
    private final TextRendererAdaptor tra = new TextRendererAdaptor();
    private final com.mygdx.stickrush.renderer.ShapeRendererAdaptor sra = new ShapeRendererAdaptor();
    private final static long fps = 50_000_000;
    private final Level level;
    private long lastUpdate;

    public LevelRenderer(Level level) {
        this.level = level;
        addTextures();
    }

    @Override
    public void addTextures() {
        sra.add(new Rectangle(dim.info, new Color(0.3f, 0.2f, 0.5f, 1)));
        sra.add(new Rectangle(dim.scene, new Color(0.4f, 0.4f, 0.5f, 1)));
        sra.add(new Rectangle(dim.sceneFloor, new Color(0.5f, 0.5f, 0.6f, 1)));
        sra.add(new Rectangle(dim.summons, new Color(0.7f, 0.5f, 0.9f, 1)));

        tra.add(new TextBoxBuilder(
          "SCORE:",
          dim.summons.x,
          dim.summons.y + dim.summons.height/2,
          dim.summons.width,
          dim.summons.height/2)
                  .setColor(1, 1, 1, 0.4f)
                  .setSize(15)
                  .center()
                  .build());
    }

    @Override
    public void render() {
        sra.drawAll();
        tra.drawAll();

        tra.draw(
          new TextBoxBuilder(level.getScore() + "", dim.summons).center().build()
        );

        level.getHero().render();
        level.getGrid().render();
    }

    @Override
    public Texture currentFrame() {
        return null;
    }

    @Override
    public void dispose() {
        level.getHero().dispose();
    }

    public void update() {
        // Impl: implement MVC superclasses for every model
        if (TimeUtils.nanoTime() - lastUpdate > fps) {
            level.getHero().nextFrame();
            lastUpdate = TimeUtils.nanoTime();
            if (camera.position.y < dim.window.height / 2) {
                camera.position.y += (dim.window.height / 2 - camera.position.y) /3;
            }
        }
        level.update();
    }
}
