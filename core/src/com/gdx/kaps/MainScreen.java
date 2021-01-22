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
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class MainScreen extends ApplicationAdapter {
	private InputHandler controller;
	private final String[] args;
	private Level level;

	public static OrthographicCamera camera;
	public static ShapeRendererAdaptor sra;
	public static TextRendererAdaptor tra;
	public static SpriteBatch batch;
	public static Dimensions dim;

	public MainScreen(String ... sdks) {
		args = sdks;
	}

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(true);

		sra = new ShapeRendererAdaptor();
		tra = new TextRendererAdaptor(25, new Color(1, 1, 1, 1));
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);

		var sidekicks = Arrays.stream(args)
						.map(sdk -> new Sidekick(SidekickRecord.ofName(sdk)))
						.collect(Collectors.toSet());

		var sdks = Sidekick.randomSetOf(2, sidekicks);

		level = new Level(
			Path.of("android/assets/levels/level" + new Random().nextInt(1)),
			sdks
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
