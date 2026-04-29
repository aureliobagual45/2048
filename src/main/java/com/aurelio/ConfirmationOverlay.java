package com.aurelio;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class ConfirmationOverlay extends StackPane {
    private final int baseAnimationMillis;
    private final VBox dialog;
    private final Label messageLabel;
    private final HBox buttons;
    private final Button confirmButton;
    private final Button cancelButton;
    private Runnable pendingAction;

    public ConfirmationOverlay(int baseAnimationMillis) {
        this.baseAnimationMillis = baseAnimationMillis;
        this.dialog = new VBox(24);
        this.messageLabel = new Label();
        this.buttons = new HBox(18);
        this.confirmButton = new Button("CONFIRM");
        this.cancelButton = new Button("CANCEL");
        this.pendingAction = null;

        setupOverlay();
        setupDialog();
        setupButton(confirmButton);
        setupButton(cancelButton);
        wireButtons();
    }

    private void setupOverlay() {
        setStyle("-fx-background-color: rgba(230, 215, 190, 0.35);");
        setVisible(false);
        setOpacity(0);
        setAlignment(Pos.CENTER);
    }

    private void setupDialog() {
        dialog.setAlignment(Pos.CENTER);
        dialog.setFillWidth(false);
        dialog.setMaxWidth(370);
        dialog.setPrefWidth(370);
        dialog.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        dialog.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        dialog.setStyle(
                "-fx-background-color: #755a2d;" +
                        "-fx-background-radius: 25;" +
                        "-fx-padding: 30 30 30 30;"
        );

        messageLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 30));
        messageLabel.setStyle("-fx-text-fill: #e6d7be;");
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);

        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(confirmButton, cancelButton);
        dialog.getChildren().addAll(messageLabel, buttons);
        getChildren().add(dialog);
    }

    private void setupButton(Button button) {
        String normalStyle =
                "-fx-background-color: rgba(230, 215, 190, 0.0);" +
                        "-fx-text-fill: #e6d7be;" +
                        "-fx-background-radius: 999;" +
                        "-fx-border-radius: 999;" +
                        "-fx-padding: 10 25 10 25;";

        String hoverStyle =
                "-fx-background-color: rgba(230, 215, 190, 0.25);" +
                        "-fx-text-fill: #e6d7be;" +
                        "-fx-background-radius: 999;" +
                        "-fx-border-radius: 999;" +
                        "-fx-padding: 10 25 10 25;";

        button.setFocusTraversable(false);
        button.setFont(Font.font("Consolas", FontWeight.BOLD, 30));
        button.setStyle(normalStyle);
        button.setOnMouseEntered(event -> button.setStyle(hoverStyle));
        button.setOnMouseExited(event -> button.setStyle(normalStyle));
    }

    private void wireButtons() {
        confirmButton.setOnAction(event -> {
            Runnable action = pendingAction;
            hide();
            if (action != null) {
                action.run();
            }
        });

        cancelButton.setOnAction(event -> hide());
    }

    public void show(String message, String confirmText, Runnable action) {
        pendingAction = action;
        messageLabel.setText(message);
        confirmButton.setText(confirmText);

        setVisible(true);
        setOpacity(0);
        dialog.setScaleX(0.9);
        dialog.setScaleY(0.9);

        FadeTransition fade = new FadeTransition(animationDuration(0.24), this);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(animationDuration(0.24), dialog);
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.setInterpolator(Interpolator.EASE_OUT);

        fade.play();
        scale.play();
    }

    public void hide() {
        FadeTransition fade = new FadeTransition(animationDuration(0.20), this);
        fade.setFromValue(getOpacity());
        fade.setToValue(0);

        fade.setOnFinished(event -> {
            setVisible(false);
            setOpacity(0);
            pendingAction = null;
        });

        fade.play();
    }

    private Duration animationDuration(double multiplier) {
        return Duration.millis(baseAnimationMillis * multiplier);
    }
}