package com.mygdx.kaps;

import com.badlogic.gdx.math.Rectangle;

public class Dimensions {
  public final float boardMargin;
  public final float sidePadding;
  public final float boxPadding;
  public final Rectangle window;
  public final Rectangle board;
  public final Rectangle tile;
  public final Rectangle bottomPanel;
  public final Rectangle boardPanel;
  public final Rectangle sidePanel;
  public final Rectangle nextBox;
  public final Rectangle holdBox;
  public final Rectangle nextGelule;
  public final Rectangle holdGelule;

  public Dimensions(int boardWidth, int boardHeight, int windowWidth, int windowHeight) {
    window = new Rectangle(0, 0, windowWidth, windowHeight);
    boardMargin = window.width / 20;

    boardPanel = new Rectangle(0, 0, window.width*2/3, window.height);
    board = new Rectangle(boardMargin, boardMargin, boardPanel.width - 2 * boardMargin, window.height);
    tile = new Rectangle(boardMargin, boardMargin, board.width / boardWidth, board.width / boardWidth);
    board.height = tile.height * boardHeight;
    boardPanel.height = board.height + 2*boardMargin;

    sidePanel = new Rectangle(boardPanel.width, 0, window.width - boardPanel.width, boardPanel.height);
    bottomPanel = new Rectangle(0, boardPanel.height, window.width, window.height - boardPanel.height);
    sidePadding = sidePanel.width / 10;

    nextBox = new Rectangle(sidePanel.x + sidePadding, boardMargin, sidePanel.width - 2 * sidePadding, sidePanel.width - 2 * sidePadding);
    holdBox = new Rectangle(sidePanel.x + sidePadding, boardMargin * 2 + nextBox.height, sidePanel.width - 2 * sidePadding, sidePanel.width - 2 * sidePadding);
    boxPadding = nextBox.width / 10;

    nextGelule = new Rectangle(nextBox.x + boxPadding, nextBox.y + boxPadding, nextBox.width - 2 * boxPadding, (nextBox.width - 2 * boxPadding) / 2);
    nextGelule.y = nextBox.y + nextBox.height/2 - nextGelule.height/2;
    holdGelule = new Rectangle(holdBox.x + boxPadding, holdBox.y + holdBox.height/2 - nextGelule.height/2, holdBox.width - 2 * boxPadding, (holdBox.width - 2 * boxPadding) / 2);
  }

  public float halfTile() {
    return tile.width/2;
  }
  public float sidePanelWithPadding() {
    return sidePanel.x + sidePadding;
  }
}
