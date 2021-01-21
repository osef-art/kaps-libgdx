package com.gdx.kaps;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.kaps.contoller.InputHandler;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.sidekick.Sidekick;
import com.gdx.kaps.level.sidekick.SidekickRecord;
import com.gdx.kaps.renderer.Dimensions;
import com.gdx.kaps.renderer.ShapeRendererAdaptor;
import com.gdx.kaps.renderer.TextRendererAdaptor;

import java.nio.file.Path;
import java.util.Random;

public class MainScreen extends ApplicationAdapter {
	private InputHandler controller;
	private Level level;

	public static OrthographicCamera camera;
	public static ShapeRendererAdaptor sra;
	public static TextRendererAdaptor tra;
	public static SpriteBatch batch;
	public static Dimensions dim;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(true);

		sra = new ShapeRendererAdaptor();
		tra = new TextRendererAdaptor(25, new Color(1, 1, 1, 1));
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);

		level = new Level(
			Path.of("android/assets/levels/level" + new Random().nextInt(21)),
			Sidekick.setOf(SidekickRecord.COLOR, SidekickRecord.XERETH)
			//Sidekick.RandomSetOf(2)
		);

		controller = new InputHandler(level);
		Gdx.input.setInputProcessor(controller);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.3f, 0.3f, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		controller.update();
		level.update();
		level.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		sra.dispose();
		tra.dispose();
	}
}
