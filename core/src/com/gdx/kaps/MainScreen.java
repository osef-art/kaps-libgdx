package com.gdx.kaps;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.gdx.kaps.contoller.InputHandler;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.grid.Position;
import com.gdx.kaps.level.sidekick.Sidekick;
import com.gdx.kaps.level.sidekick.SidekickRecord;
import com.gdx.kaps.renderer.*;
import com.gdx.kaps.time.Timer;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class MainScreen extends ApplicationAdapter {
	private InputHandler controller;
	private final String[] args;
	private Vector2 cameraPos;
	private static Position shaking;
	private static Timer timer;
	private Level level;

	public static OrthographicCamera camera;
	public static SpriteRendererAdaptor spra;
	public static ShapeRendererAdaptor sra;
	public static TextRendererAdaptor tra;
	public static Dimensions dim;

	public MainScreen(String ... sdks) {
		args = sdks;
	}

	public static void shake() {
		timer.reset();
		shaking.add(
			new Random().nextBoolean() ? 25 : -25,
			new Random().nextBoolean() ? 25 : -25
		);
	}

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(true);
		cameraPos = new Vector2(camera.position.x, camera.position.y);

		spra = new SpriteRendererAdaptor();
		sra = new ShapeRendererAdaptor();
		tra = new TextRendererAdaptor(25, new Color(1, 1, 1, 1));

		var sidekicks = Arrays.stream(args)
						.map(SidekickRecord::ofName)
						.collect(Collectors.toSet());

		level = new Level(
			Path.of("android/assets/levels/level" + new Random().nextInt(21)),
			Sidekick.randomSetOf(2, sidekicks)
		);
		camera.translate(0, dim.get(Zone.WINDOW).height);
		controller = new InputHandler(level);
		Gdx.input.setInputProcessor(controller);

		shaking = new Position();
		timer = new Timer(10_000_000);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.3f, 0.3f, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (camera.position.y > cameraPos.y) {
			camera.position.y -= (camera.position.y - cameraPos.y) / 12.5;
			camera.update();
		}
		// TODO: finish
		if (timer.resetIfExceeds() && shaking.x() != 0) {
			shaking.add((int) Math.signum(-shaking.x()), (int) Math.signum(-shaking.y()));
			camera.position.set(cameraPos.x + shaking.x(), cameraPos.y + shaking.y(), 0);
			camera.update();
		}

		controller.update();
		level.update();
		level.render();
	}
	
	@Override
	public void dispose () {
		spra.dispose();
		sra.dispose();
		tra.dispose();
	}
}
