package com.github.tth05.minesweeper;

import javafx.scene.control.Label;

public class Cell extends Label {

    private final int x;
    private final int y;
    private int bombCount = 0;
    private boolean isBomb = false;
    private boolean isMarked = false;
    private boolean isHidden = true;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
        if (!hidden)
            getStyleClass().addAll("open", isBomb ? "bomb" : "n" + bombCount);
        else
            getStyleClass().removeAll("open", "bomb", "n" + bombCount);
    }

    public boolean isBomb() {
        return isBomb;
    }

    public void setBomb(boolean bomb) {
        isBomb = bomb;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
        if (marked)
            getStyleClass().add("marked");
        else
            getStyleClass().remove("marked");
    }

    public int getBombCount() {
        return bombCount;
    }

    public void setBombCount(int bombCount) {
        this.bombCount = bombCount;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
