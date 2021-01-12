package com.mygdx.kaps;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.kaps.level.Level;
import com.mygdx.kaps.level.Options;

import javax.swing.*;

public class Kaps extends ApplicationAdapter {
	public static OrthographicCamera camera;
	public static Dimensions dimensions;
    public static Renderer renderer;
	public static SpriteBatch batch;
	public static Options options;
	public static Level level;

	@Override
	public void create() {
		dimensions = new Dimensions(6, 12, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera = new OrthographicCamera();
		renderer = new Renderer();
		options = new Options();
		batch = new SpriteBatch();
		level = new Level(6, 12);

		camera.setToOrtho(true, dimensions.window.width, dimensions.window.height);
		camera.position.y += dimensions.window.height;
		Gdx.input.setInputProcessor(new InputHandler());

		batch.setProjectionMatrix(camera.combined);
		renderer.setProjectionMatrix(camera.combined);
	}

	@Override
	public void render() {
		if (Gdx.input.isKeyPressed(Input.Keys.Q)) System.exit(0);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();

		level.control();
		level.update();
		level.render();
	}

	public static void draw(Sprite sprite, Rectangle rect) {
		draw(sprite, rect.x, rect.y, rect.width, rect.height);
	}

	public static void draw(Sprite sprite, float x, float y, float width, float height) {
		batch.begin();
		batch.draw( sprite, x, y, width, height);
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		renderer.dispose();
	}
}
