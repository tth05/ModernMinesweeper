package com.github.kotliniscool;

import com.github.kotliniscool.game.Cell;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends Application {

    private final int BOMB_COUNT = 15;
    private final int TILES = 10;
    private final int TILE_SIZE = 30;
    private final int PADDING = 3;
    private final int MARGIN = 5;
    private Cell[][] grid = new Cell[TILES][TILES];
    private Pane root = new Pane();

    @Override
    public void start(Stage primaryStage) {
        populateGrid();
        root.getStylesheets().add("resources/style.css");
        primaryStage.setTitle("Minesweeper");
        primaryStage.setResizable(true);
        primaryStage.setScene(new Scene(root, ((TILES - 1) * PADDING + TILES * TILE_SIZE) + MARGIN * 2, ((TILES - 1) * PADDING + TILES * TILE_SIZE) + MARGIN * 2));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    private void populateGrid() {
        //Add cells
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

        //Add bombs
        for (int i = 0; i < BOMB_COUNT; i++) {
            Cell cell = grid[ThreadLocalRandom.current().nextInt(TILES)][ThreadLocalRandom.current().nextInt(TILES)];
            if (cell.isBomb()) i--;
            else cell.setBomb(true);
        }

        //Give the cells their numbers
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
        Cell cell = findInGrid(event.getSource());
        if (cell == null) return;
        if (!cell.isHidden()) return;
        if (event.getButton() == MouseButton.SECONDARY) {
            cell.setMarked(!cell.isMarked());
        }
        else if (cell.isBomb() && event.getButton() == MouseButton.PRIMARY) {
            endGame();
            return;
        }
        else if (cell.getBombCount() > 0) {
            cell.setHidden(false);
        }
        else if (event.getButton() == MouseButton.PRIMARY) {
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
        showAlert("You won!");
    }

    //Recursive flood fill algorithm
    private void floodFill(int i, int j) {
        for (int x = i - 1 < 0 ? 0 : i - 1; x <= (i + 1 > TILES - 1 ? TILES - 1 : i + 1); x++) {
            for (int y = j - 1 < 0 ? 0 : j - 1; y <= (j + 1 > TILES - 1 ? TILES - 1 : j + 1); y++) {
                if (!grid[x][y].isBomb() && grid[x][y].isHidden()) {
                    grid[x][y].setHidden(false);
                    grid[x][y].setMarked(false);
                    if (grid[x][y].getBombCount() < 1)
                        floodFill(x, y);
                }
            }
        }
    }

    private Cell findInGrid(Object cell) {
        if (cell instanceof Cell) {
            for (int i = 0; i < TILES; i++) {
                for (int j = 0; j < TILES; j++) {
                    if (grid[i][j].equals(cell))
                        return grid[i][j];
                }
            }
        }
        return null;
    }

    private void showAlert(String message/*, String title*/) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Minesweeper");
//        alert.setHeaderText(title);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            root.getChildren().clear();
            populateGrid();
        }
    }

    private void endGame() {
        for (int i = 0; i < TILES; i++) {
            for (int j = 0; j < TILES; j++) {
                grid[i][j].setHidden(false);
            }
        }
        showAlert("You lost");
    }


    public static void main(String[] args) {
        launch(args);
    }
}
