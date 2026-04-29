package com.aurelio;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TileFactory {
    private final int tileSize;

    public TileFactory(int tileSize) {
        this.tileSize = tileSize;
    }

    public StackPane createTile(int value) {
        StackPane tile = new StackPane();
        tile.setPrefSize(tileSize, tileSize);
        tile.setMinSize(tileSize, tileSize);
        tile.setMaxSize(tileSize, tileSize);
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

    public StackPane createFloatingTile(int value, int row, int col, double x, double y) {
        StackPane tile = createTile(value);
        tile.setManaged(false);
        tile.setPrefSize(tileSize, tileSize);
        tile.resize(tileSize, tileSize);
        tile.setLayoutX(x);
        tile.setLayoutY(y);
        return tile;
    }

    private double getFontSize(int value) {
        if (value == 0) {
            return 0;
        }

        int digits = String.valueOf(value).length();
        double size = 80 - digits * 8;
        return Math.max(size, 16);
    }

    private Color getTileColorObject(int value) {
        if (value == 0) {
            return Color.web("#d5cdc4");
        }

        int level = (int) (Math.log(value) / Math.log(2));
        double t = Math.min(level / 11.0, 1.0);

        double startHue = 40;
        double endHue = 275;
        double delta = ((endHue - startHue + 360) % 360) - 360;
        double hue = (startHue + delta * t + 360) % 360;
        double saturation = 0.75;
        double brightness = 0.95 - 0.35 * t;

        return Color.hsb(hue, saturation, brightness);
    }

    private String toHex(Color color) {
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        return String.format("#%02x%02x%02x", r, g, b);
    }
}
