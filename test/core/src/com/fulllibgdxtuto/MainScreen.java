package com.fulllibgdxtuto;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class MainScreen extends ApplicationAdapter {

    private B2dModel model;
    private OrthographicCamera cam;
    private Box2DDebugRenderer debugRenderer;

    @Override
    public void create () {
        model = new B2dModel();
        cam = new OrthographicCamera(32,24);
        debugRenderer = new Box2DDebugRenderer(true,true,true,true,true,true);
    }

    @Override
    public void render () {
        model.logicStep(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        debugRenderer.render(model.world, cam.combined);
    }

    @Override
    public void dispose () {
    }
}
