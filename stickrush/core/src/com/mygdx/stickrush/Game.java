package com.mygdx.stickrush;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.Gdx.graphics;

public class Game extends ApplicationAdapter {
	private LevelRenderer levelRenderer;
	public static OrthographicCamera camera;
	static Dimensions dim;
	static Cursor cursor;
	static Level level;

	@Override
	public void create() {
		cursor = new Cursor();
		dim = new Dimensions(graphics.getWidth(), graphics.getHeight());
		camera = new OrthographicCamera();
		camera.setToOrtho(false, dim.window.width, dim.window.height);
		camera.translate(0, -dim.window.height);

		level = new Level(new Grid(10, 10));
		levelRenderer = new LevelRenderer(level);
		Gdx.input.setInputProcessor(new InputHandler());
	}

	@Override
	public void render() {
		gl.glClearColor(0.3f, 0.2f, 0.5f, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();

		if (Gdx.input.isKeyPressed(Input.Keys.Q)) System.exit(0);
		level.update();

		levelRenderer.update();
		levelRenderer.render();
		cursor.render();
	}
	
	@Override
	public void dispose() {
		levelRenderer.dispose();
	}
}
