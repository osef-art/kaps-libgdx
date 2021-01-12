package com.mygdx.tetris;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.tetris.board.Board;
import com.mygdx.tetris.board.shape.block.Coords;
import com.mygdx.tetris.board.shape.block.Tile;

public class Tetris extends ApplicationAdapter {
  private OrthographicCamera camera;
  private SpriteBatch batch;
  private static ShapeRenderer shapeRenderer;
  private static Coords dimensions;

  private Timer timer, moveSpeed, flipSpeed;
  private Board board;

  public static ShapeRenderer shapeRenderer() {
    return shapeRenderer;
  }
  public static Coords dimensions() {
    return dimensions;
  }

  @Override
  public void create () {
    shapeRenderer = new ShapeRenderer();
    dimensions = new Coords(480, 800);
    camera = new OrthographicCamera();
    camera.setToOrtho(false, dimensions.x(), dimensions.y());
    batch = new SpriteBatch();

    board = new Board(10, 17);
    timer = new Timer(1);
    moveSpeed = new Timer(0.07);
    flipSpeed = new Timer(0.15);
  }

  @Override
  public void render () {
    if (Gdx.input.isKeyPressed(Input.Keys.Q)) System.exit(0);
    Gdx.gl.glClearColor(0, 0.05f, 0.1f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    camera.update();
    batch.setProjectionMatrix(camera.combined);

    if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && moveSpeed.isExceeded()) {
      moveSpeed.reset();
      board.moveShapeLeft();
    }
    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && moveSpeed.isExceeded()) {
      moveSpeed.reset();
      board.moveShapeRight();
    }
    if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && moveSpeed.isExceeded()) {
      moveSpeed.reset();
      board.dipShape();
    }
    if (Gdx.input.isKeyPressed(Input.Keys.UP) && flipSpeed.isExceeded()) {
      flipSpeed.reset();
      board.flipShape();
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
      board.dropShape();
      timer.reset(timer.limit() - 0.005);
    }

    if (timer.isExceeded()) {
      board.dipShape();
      timer.reset();
    }

    board.draw();
    batch.begin();
    batch.draw(board.next().img(),
      75 + board.width() * Tile.dimensions().x(),
      dimensions.y() - 125,
      75, 75);
    batch.end();
  }

  @Override
  public void dispose () {
  }
}
