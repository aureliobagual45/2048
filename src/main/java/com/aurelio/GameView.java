package com.aurelio;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameView {
    private static final int GRID_SIZE = 4;
    private static final int TILE_SIZE = 120;

    private final GridPane root;

    public GameView() {
        root = new GridPane();
        setupLayout();
    }

    public GridPane getRoot() {
        return root;
    }

    private void setupLayout() {
        root.setAlignment(Pos.CENTER);
        root.setFocusTraversable(true);
        root.setHgap(10);
        root.setVgap(10);

        for (int i = 0; i < GRID_SIZE; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setMinWidth(TILE_SIZE);
            col.setPrefWidth(TILE_SIZE);
            col.setMaxWidth(TILE_SIZE);
            root.getColumnConstraints().add(col);

            RowConstraints row = new RowConstraints();
            row.setMinHeight(TILE_SIZE);
            row.setPrefHeight(TILE_SIZE);
            row.setMaxHeight(TILE_SIZE);
            root.getRowConstraints().add(row);
        }
    }

    public void render(GameState gameState) {
        root.getChildren().clear();

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                StackPane tile = createTile(gameState.getValue(row, col));
                root.add(tile, col, row);
            }
        }
    }

    private StackPane createTile(int value) {
        StackPane tile = new StackPane();
        tile.setPrefSize(TILE_SIZE, TILE_SIZE);
        tile.setMinSize(TILE_SIZE, TILE_SIZE);
        tile.setMaxSize(TILE_SIZE, TILE_SIZE);
        tile.setAlignment(Pos.CENTER);

        Color baseColor = getTileColorObject(value);

        tile.setStyle(
                "-fx-background-color: " + toHex(baseColor) + ";" +
                        "-fx-background-radius: 10;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setRadius(4);
        shadow.setOffsetX(4);
        shadow.setOffsetY(4);
        shadow.setColor(baseColor.darker());
        tile.setEffect(shadow);

        if (value != 0) {
            Label label = new Label(String.valueOf(value));
            label.setFont(Font.font("Consolas", FontWeight.BOLD, getFontSize(value)));
            label.setStyle("-fx-text-fill: white;");
            label.setTranslateX(-2);
            label.setTranslateY(-2);
            tile.getChildren().add(label);
        }

        return tile;
    }

    private Color getTileColorObject(int value) {
        if (value == 0) {
            return Color.web("#d5cdc4");
        }

        int level = getLevel(value);
        double t = Math.min(level / 11.0, 1.0);

        double startHue = 40;
        double endHue = 275;
        double delta = ((endHue - startHue + 360) % 360) - 360;
        double hue = (startHue + delta * t + 360) % 360;
        double saturation = 0.75;
        double brightness = 0.95 - 0.35 * t;

        return Color.hsb(hue, saturation, brightness);
    }

    private double getFontSize(int value) {
        if (value == 0) {
            return 0;
        }

        int digits = String.valueOf(value).length();
        double size = 80 - digits * 8;
        return Math.max(size, 16);
    }

    private String toHex(Color color) {
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);

        return String.format("#%02x%02x%02x", r, g, b);
    }

    private int getLevel(int value) {
        return (int) (Math.log(value) / Math.log(2));
    }
}
