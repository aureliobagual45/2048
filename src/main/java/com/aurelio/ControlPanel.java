package com.aurelio;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.Objects;

public class ControlPanel extends VBox {
    private static final double COLLAPSED_WIDTH = 100;
    private static final double EXPANDED_WIDTH = 150;
    private static final double COLLAPSED_HEIGHT = 270;
    private static final double EXPANDED_HEIGHT = 400;
    private static final double COLLAPSED_SPACING = 0;
    private static final double EXPANDED_SPACING = 30;
    private static final double COLLAPSED_BUTTON_SIZE = 80;
    private static final double EXPANDED_BUTTON_SIZE = 100;
    private static final double COLLAPSED_ICON_SIZE = 50;
    private static final double EXPANDED_ICON_SIZE = 62;

    private final int baseAnimationMillis;
    private final Button undoButton;
    private final Button resetButton;
    private final Button exitButton;
    private Timeline animation;

    public ControlPanel(int baseAnimationMillis) {
        this.baseAnimationMillis = baseAnimationMillis;
        this.undoButton = new Button("@");
        this.resetButton = new Button("#");
        this.exitButton = new Button("$");
        this.animation = null;

        setupPanel();
        setupButton(undoButton, "/images/undo.png");
        setupButton(resetButton, "/images/reset.png");
        setupButton(exitButton, "/images/exit.png");
    }

    private void setupPanel() {
        setAlignment(Pos.CENTER);
        setFillWidth(false);
        setSpacing(COLLAPSED_SPACING);
        setPrefWidth(COLLAPSED_WIDTH);
        setPrefHeight(COLLAPSED_HEIGHT);
        setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setStyle(
                "-fx-background-color: #755a2d;" +
                        "-fx-background-radius: 0 25 25 0;" +
                        "-fx-padding: 20 0 20 0;"
        );

        getChildren().addAll(undoButton, resetButton, exitButton);

        setOnMouseEntered(event -> animatePanel(EXPANDED_WIDTH, EXPANDED_HEIGHT, EXPANDED_SPACING));
        setOnMouseExited(event -> animatePanel(COLLAPSED_WIDTH, COLLAPSED_HEIGHT, COLLAPSED_SPACING));
    }

    private void setupButton(Button button, String imagePath) {
        String normalStyle =
                "-fx-background-color: rgba(230, 215, 190, 0.0);" +
                        "-fx-text-fill: #e6d7be;" +
                        "-fx-background-radius: 999;" +
                        "-fx-border-radius: 999;" +
                        "-fx-padding: 10 20 10 20;";

        String hoverStyle =
                "-fx-background-color: rgba(230, 215, 190, 0.25);" +
                        "-fx-text-fill: #e6d7be;" +
                        "-fx-background-radius: 999;" +
                        "-fx-border-radius: 999;" +
                        "-fx-padding: 10 20 10 20;";

        Image image = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream(imagePath)
        ));
        ImageView icon = new ImageView(image);
        icon.setFitWidth(COLLAPSED_ICON_SIZE);
        icon.setFitHeight(COLLAPSED_ICON_SIZE);
        icon.setPreserveRatio(true);

        button.setText(null);
        button.setGraphic(icon);
        button.setFocusTraversable(false);
        button.setMinSize(COLLAPSED_BUTTON_SIZE, COLLAPSED_BUTTON_SIZE);
        button.setPrefSize(COLLAPSED_BUTTON_SIZE, COLLAPSED_BUTTON_SIZE);
        button.setMaxSize(COLLAPSED_BUTTON_SIZE, COLLAPSED_BUTTON_SIZE);
        button.setStyle(normalStyle);

        button.setOnMouseEntered(event -> button.setStyle(hoverStyle));
        button.setOnMouseExited(event -> button.setStyle(normalStyle));
    }

    public void setOnUndo(Runnable action) {
        undoButton.setOnAction(event -> action.run());
    }

    public void setOnReset(Runnable action) {
        resetButton.setOnAction(event -> action.run());
    }

    public void setOnExit(Runnable action) {
        exitButton.setOnAction(event -> action.run());
    }

    private void animatePanel(double targetWidth, double targetHeight, double targetSpacing) {
        if (animation != null) {
            animation.stop();
        }

        boolean expanding = targetWidth > getPrefWidth();
        double targetButtonSize = expanding ? EXPANDED_BUTTON_SIZE : COLLAPSED_BUTTON_SIZE;
        double targetIconSize = expanding ? EXPANDED_ICON_SIZE : COLLAPSED_ICON_SIZE;

        animation = new Timeline(
                new KeyFrame(
                        animationDuration(0.3),
                        new KeyValue(prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(prefHeightProperty(), targetHeight, Interpolator.EASE_BOTH),
                        new KeyValue(spacingProperty(), targetSpacing, Interpolator.EASE_BOTH),
                        new KeyValue(undoButton.prefWidthProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(undoButton.prefHeightProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(undoButton.minWidthProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(undoButton.minHeightProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(undoButton.maxWidthProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(undoButton.maxHeightProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(((ImageView) undoButton.getGraphic()).fitWidthProperty(), targetIconSize, Interpolator.EASE_BOTH),
                        new KeyValue(((ImageView) undoButton.getGraphic()).fitHeightProperty(), targetIconSize, Interpolator.EASE_BOTH),
                        new KeyValue(resetButton.prefWidthProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(resetButton.prefHeightProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(resetButton.minWidthProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(resetButton.minHeightProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(resetButton.maxWidthProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(resetButton.maxHeightProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(((ImageView) resetButton.getGraphic()).fitWidthProperty(), targetIconSize, Interpolator.EASE_BOTH),
                        new KeyValue(((ImageView) resetButton.getGraphic()).fitHeightProperty(), targetIconSize, Interpolator.EASE_BOTH),
                        new KeyValue(exitButton.prefWidthProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(exitButton.prefHeightProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(exitButton.minWidthProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(exitButton.minHeightProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(exitButton.maxWidthProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(exitButton.maxHeightProperty(), targetButtonSize, Interpolator.EASE_BOTH),
                        new KeyValue(((ImageView) exitButton.getGraphic()).fitWidthProperty(), targetIconSize, Interpolator.EASE_BOTH),
                        new KeyValue(((ImageView) exitButton.getGraphic()).fitHeightProperty(), targetIconSize, Interpolator.EASE_BOTH)
                )
        );

        animation.play();
    }

    private Duration animationDuration(double multiplier) {
        return Duration.millis(baseAnimationMillis * multiplier);
    }
}
