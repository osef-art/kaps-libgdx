package com.gdx.kaps;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.gdx.kaps.contoller.LevelController;
import com.gdx.kaps.level.Level;
import com.gdx.kaps.level.sidekick.Sidekick;
import com.gdx.kaps.level.sidekick.SidekickRecord;
import com.gdx.kaps.renderer.*;
import com.gdx.kaps.time.Timer;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class MainScreen extends ApplicationAdapter {
	private final Set<SidekickRecord> sidekicks;
	private LevelController controller;
	private Vector2 cameraPos;
	private Level level;

	public static OrthographicCamera camera;
	public static SpriteRendererAdaptor spra;
	public static TextRendererAdaptor tra25;
	public static TextRendererAdaptor tra15;
	public static ShapeRendererAdaptor sra;
	//IMPL: move to Level
	public static Dimensions dim;
	private static Vector2 shaking;
	private static Timer timer;

	public MainScreen(String ... sdks) {
		sidekicks = Arrays.stream(sdks)
					.map(SidekickRecord::ofName)
					.collect(Collectors.toSet());
	}

	@Override
	public void create () {
		camera = new OrthographicCamera();
		camera.setToOrtho(true);
		cameraPos = new Vector2(camera.position.x, camera.position.y);

		spra = new SpriteRendererAdaptor();
		sra = new ShapeRendererAdaptor();
		tra25 = new TextRendererAdaptor(25, Color.WHITE);
		tra15 = new TextRendererAdaptor(15, Color.WHITE);

		level = new Level(
			Path.of("android/assets/levels/level" + new Random().nextInt(21)),
			Sidekick.randomSetOf(2, sidekicks)
		);
		camera.translate(0, dim.get(Zone.WINDOW).height);
		controller = new LevelController(level);
		Gdx.input.setInputProcessor(controller);

		shaking = new Vector2();
		timer = new Timer(25_000_000);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.3f, 0.3f, 0.4f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		moveCamera();

		controller.update();
		level.update();
		level.render();
	}

	public static void shake() {
		timer.reset();
		shaking.add(
		new Random().nextBoolean() ? 5 : -5,
		new Random().nextBoolean() ? 5 : -5
		);
	}

	private void moveCamera() {
		if (shaking.x != 0) {
			// shake
			if (timer.resetIfExceeds()) {
				shaking.set(-shaking.x + Math.signum(shaking.x)*0.5f, -shaking.y + Math.signum(shaking.y)*0.5f);
				camera.position.set(cameraPos.x + shaking.x, cameraPos.y + shaking.y, 0);
				camera.update();
			}
		} else {
			// slide
			if (camera.position.y > cameraPos.y) {
				camera.position.y -= (camera.position.y - cameraPos.y) / 12.5;
				camera.update();
			}
		}
	}

	@Override
	public void dispose () {
		spra.dispose();
		sra.dispose();
		tra25.dispose();
	}
}
