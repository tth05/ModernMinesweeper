package com.github.kotliniscool;

import com.github.kotliniscool.game.Cell;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.concurrent.ThreadLocalRandom;

public class Game extends Application {

    private static final int BOMB_COUNT = 10;
    private static final int TILES = 20;
    private static final int TILE_SIZE = 30;
    private static final int PADDING = 3;
    private static final int MARGIN = 5;
    private Cell[][] grid = new Cell[TILES][TILES];

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        populateGrid(root);
        root.getStylesheets().add("resources/style.css");
        primaryStage.setTitle("Minesweeper");
        primaryStage.setResizable(true);
        primaryStage.setScene(new Scene(root, ((TILES - 1) * PADDING + TILES * TILE_SIZE) + MARGIN * 2, ((TILES - 1) * PADDING + TILES * TILE_SIZE) + MARGIN * 2));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    private void populateGrid(Pane root) {
        for (int i = 0; i < TILES; i++) {
            for (int j = 0; j < TILES; j++) {
                Cell cell = new Cell(i, j);
                cell.setTranslateX((i * TILE_SIZE + PADDING * i) + MARGIN);
                cell.setTranslateY((j * TILE_SIZE + PADDING * j) + MARGIN);
                cell.setPrefSize(TILE_SIZE, TILE_SIZE);
                cell.getStyleClass().addAll("cell");
                cell.setOnMouseClicked(this::handleClick);
                grid[i][j] = cell;
                root.getChildren().add(cell);
            }
        }

        for (int i = 0; i < BOMB_COUNT; i++) {
            Cell cell = grid[ThreadLocalRandom.current().nextInt(BOMB_COUNT)][ThreadLocalRandom.current().nextInt(BOMB_COUNT)];
            if (cell.isBomb()) i--;
            else cell.setBomb(true);
        }

        for (int i = 0; i < TILES; i++) {
            for (int j = 0; j < TILES; j++) {
                if (grid[i][j].isBomb()) continue;
                int count = 0;
                for (int x = i - 1 < 0 ? 0 : i - 1; x <= (i + 1 > TILES - 1 ? TILES - 1 : i + 1); x++) {
                    for (int y = j - 1 < 0 ? 0 : j - 1; y <= (j + 1 > TILES - 1 ? TILES - 1 : j + 1); y++) {
                        if (grid[x][y].isBomb()) count++;
                    }
                }
                grid[i][j].setBombCount(count);
            }
        }
    }

    private void handleClick(MouseEvent event) {
        Cell cell = findInGrid((Cell) event.getSource());
        if (cell == null) return;
        if (!cell.isHidden()) return;
        if (event.getButton() == MouseButton.SECONDARY) {
            cell.setMarked(!cell.isMarked());
        }
        if (cell.isBomb() && event.getButton() == MouseButton.PRIMARY) {
            endGame();
            return;
        }
        if (cell.getBombCount() > 0) {
            cell.setHidden(false);
        }
        if (event.getButton() == MouseButton.PRIMARY) {
            floodFill(cell.getX(), cell.getY());
        }
        checkWin();
    }

    private void checkWin() {
        for (int i = 0; i < TILES; i++) {
            for (int j = 0; j < TILES; j++) {
                if (!grid[i][j].isBomb() && grid[i][j].isHidden()) return;
            }
        }
        System.out.println("You won");
    }

    private void floodFill(int i, int j) {
        for (int x = i - 1 < 0 ? 0 : i - 1; x <= (i + 1 > TILES - 1 ? TILES - 1 : i + 1); x++) {
            for (int y = j - 1 < 0 ? 0 : j - 1; y <= (j + 1 > TILES - 1 ? TILES - 1 : j + 1); y++) {
                if (!grid[x][y].isBomb() && grid[x][y].isHidden()) {
                    grid[x][y].setHidden(false);
                    if (grid[x][y].getBombCount() < 1)
                        floodFill(x, y);
                }
            }
        }
    }

    private Cell findInGrid(Cell cell) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                if (grid[i][j].equals(cell))
                    return grid[i][j];
            }
        }
        return null;
    }

    private void endGame() {
        Platform.exit();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
