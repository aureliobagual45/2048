package com.aurelio;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class GameOverOverlay extends StackPane {
    private final int baseAnimationMillis;
    private final Label gameOverLabel;
    private final Button restartButton;
    private final Button undoButton;
    private final HBox actionButtons;
    private final VBox content;
    private final GaussianBlur blur;

    public GameOverOverlay(int baseAnimationMillis) {
        this.baseAnimationMillis = baseAnimationMillis;
        this.gameOverLabel = new Label("GAME OVER");
        this.restartButton = new Button("RESTART");
        this.undoButton = new Button("UNDO");
        this.actionButtons = new HBox(20, restartButton, undoButton);
        this.content = new VBox(40, gameOverLabel, actionButtons);
        this.blur = new GaussianBlur(0);

        setupOverlay();
        setupLabel();
        setupActionButton(restartButton);
        setupActionButton(undoButton);
        setupContent();
    }

    public void setOnRestart(Runnable action) {
        restartButton.setOnAction(event -> action.run());
    }

    public void setOnUndo(Runnable action) {
        undoButton.setOnAction(event -> action.run());
    }

    public void show(Node boardNode) {
        setVisible(true);
        setOpacity(0);

        gameOverLabel.setOpacity(1);
        gameOverLabel.setScaleX(150);
        gameOverLabel.setScaleY(150);

        restartButton.setOpacity(0);
        restartButton.setScaleX(0);
        restartButton.setScaleY(0);
        undoButton.setOpacity(0);
        undoButton.setScaleX(0);
        undoButton.setScaleY(0);

        boardNode.setEffect(blur);

        Timeline blurAnimation = new Timeline(
                new KeyFrame(animationDuration(1.0),
                        new KeyValue(blur.radiusProperty(), 12))
        );

        FadeTransition fadeOverlay = new FadeTransition(animationDuration(1.0), this);
        fadeOverlay.setFromValue(0);
        fadeOverlay.setToValue(1);

        ScaleTransition scaleGameOver = new ScaleTransition(animationDuration(1.0), gameOverLabel);
        scaleGameOver.setFromX(150);
        scaleGameOver.setFromY(150);
        scaleGameOver.setToX(10);
        scaleGameOver.setToY(10);
        scaleGameOver.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fadeRestart = new FadeTransition(animationDuration(1.0), restartButton);
        fadeRestart.setFromValue(0);
        fadeRestart.setToValue(1);

        FadeTransition fadeUndo = new FadeTransition(animationDuration(1.0), undoButton);
        fadeUndo.setFromValue(0);
        fadeUndo.setToValue(1);

        ScaleTransition scaleRestart = new ScaleTransition(animationDuration(1.0), restartButton);
        scaleRestart.setFromX(0);
        scaleRestart.setFromY(0);
        scaleRestart.setToX(1);
        scaleRestart.setToY(1);
        scaleRestart.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition scaleUndo = new ScaleTransition(animationDuration(1.0), undoButton);
        scaleUndo.setFromX(0);
        scaleUndo.setFromY(0);
        scaleUndo.setToX(1);
        scaleUndo.setToY(1);
        scaleUndo.setInterpolator(Interpolator.EASE_OUT);

        scaleGameOver.setOnFinished(event -> {
            fadeRestart.play();
            scaleRestart.play();
            fadeUndo.play();
            scaleUndo.play();
        });

        blurAnimation.play();
        fadeOverlay.play();
        scaleGameOver.play();
    }

    public void hide(Node boardNode) {
        FadeTransition fadeOutOverlay = new FadeTransition(animationDuration(1.0), this);
        fadeOutOverlay.setFromValue(getOpacity());
        fadeOutOverlay.setToValue(0);

        FadeTransition fadeOutLabel = new FadeTransition(animationDuration(1.0), gameOverLabel);
        fadeOutLabel.setFromValue(gameOverLabel.getOpacity());
        fadeOutLabel.setToValue(0);

        FadeTransition fadeOutRestart = new FadeTransition(animationDuration(1.0), restartButton);
        fadeOutRestart.setFromValue(restartButton.getOpacity());
        fadeOutRestart.setToValue(0);

        FadeTransition fadeOutUndo = new FadeTransition(animationDuration(1.0), undoButton);
        fadeOutUndo.setFromValue(undoButton.getOpacity());
        fadeOutUndo.setToValue(0);

        if (boardNode.getEffect() instanceof GaussianBlur currentBlur) {
            Timeline blurOut = new Timeline(
                    new KeyFrame(animationDuration(1.0),
                            new KeyValue(currentBlur.radiusProperty(), 0))
            );
            blurOut.setOnFinished(event -> boardNode.setEffect(null));
            blurOut.play();
        }

        fadeOutOverlay.setOnFinished(event -> {
            setVisible(false);
            setOpacity(0);
            gameOverLabel.setOpacity(1);
            gameOverLabel.setScaleX(1);
            gameOverLabel.setScaleY(1);
            restartButton.setOpacity(1);
            restartButton.setScaleX(1);
            restartButton.setScaleY(1);
            undoButton.setOpacity(1);
            undoButton.setScaleX(1);
            undoButton.setScaleY(1);
            boardNode.setEffect(null);
        });

        fadeOutOverlay.play();
        fadeOutLabel.play();
        fadeOutRestart.play();
        fadeOutUndo.play();
    }

    private void setupOverlay() {
        setStyle("-fx-background-color: rgba(230, 215, 190, 0.5);");
        setVisible(false);
        setOpacity(0);
    }

    private void setupLabel() {
        gameOverLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 10));
        gameOverLabel.setStyle("-fx-text-fill: #533f20;");
    }

    private void setupActionButton(Button button) {
        String normalStyle =
                "-fx-background-color: rgba(117, 90, 45, 0.0);" +
                        "-fx-text-fill: #533f20;" +
                        "-fx-background-radius: 999;" +
                        "-fx-border-radius: 999;" +
                        "-fx-padding: 10 30 10 30;";

        String hoverStyle =
                "-fx-background-color: rgba(117, 90, 45, 0.25);" +
                        "-fx-text-fill: #533f20;" +
                "-fx-background-radius: 999;" +
                        "-fx-border-radius: 999;" +
                        "-fx-padding: 10 30 10 30;";

        button.setFocusTraversable(false);
        button.setFont(Font.font("Consolas", FontWeight.BOLD, 40));
        button.setStyle(normalStyle);
        button.setOnMouseEntered(event -> button.setStyle(hoverStyle));
        button.setOnMouseExited(event -> button.setStyle(normalStyle));
    }

    private void setupContent() {
        content.setAlignment(javafx.geometry.Pos.CENTER);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);
        getChildren().add(content);
    }

    private Duration animationDuration(double multiplier) {
        return Duration.millis(baseAnimationMillis * multiplier);
    }
}
