package com.aurelio;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardPanel extends StackPane {
    private static final double COLLAPSED_WIDTH = 100;
    private static final double EXPANDED_WIDTH = 350;
    private static final double COLLAPSED_HEIGHT = 270;
    private static final double EXPANDED_HEIGHT = 500;

    private final int baseAnimationMillis;
    private final Text collapsedTitleLabel;
    private final VBox expandedContent;
    private final Label expandedTitleLabel;
    private final GridPane table;
    private final Label emptyLabel;
    private final List<LeaderboardEntry> entries;
    private Timeline sizeAnimation;
    private ParallelTransition contentAnimation;

    public LeaderboardPanel(int baseAnimationMillis) {
        this.baseAnimationMillis = baseAnimationMillis;
        this.collapsedTitleLabel = new Text("LEADERBOARD");
        this.expandedContent = new VBox(18);
        this.expandedTitleLabel = new Label("LEADERBOARD");
        this.table = new GridPane();
        this.emptyLabel = new Label("No scores yet.");
        this.entries = new ArrayList<>();
        this.sizeAnimation = null;
        this.contentAnimation = null;

        setupPanel();
        setupCollapsedTitle();
        setupExpandedContent();
        setupTable();
        setupEmptyLabel();
        wireHover();
        refreshTable();
    }

    public void setEntries(List<LeaderboardEntry> entries) {
        this.entries.clear();
        this.entries.addAll(entries);
        refreshTable();
    }

    private void setupPanel() {
        setAlignment(Pos.CENTER);
        setPrefWidth(COLLAPSED_WIDTH);
        setPrefHeight(COLLAPSED_HEIGHT);
        setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setStyle(
                "-fx-background-color: #755a2d;" +
                        "-fx-background-radius: 25 0 0 25;" +
                        "-fx-padding: 20 20 20 20;"
        );

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);

        getChildren().addAll(expandedContent, collapsedTitleLabel);
    }

    private void setupCollapsedTitle() {
        collapsedTitleLabel.setRotate(90);
        collapsedTitleLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 30));
        collapsedTitleLabel.setStyle("-fx-fill: #e6d7be;");
        collapsedTitleLabel.setOpacity(1.0);
    }

    private void setupExpandedContent() {
        expandedContent.setAlignment(Pos.TOP_CENTER);
        expandedContent.setFillWidth(false);
        expandedContent.setOpacity(0.0);

        expandedTitleLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 32));
        expandedTitleLabel.setStyle("-fx-text-fill: #e6d7be;");

        expandedContent.getChildren().addAll(expandedTitleLabel, table, emptyLabel);
    }

    private void setupTable() {
        table.setHgap(10);
        table.setVgap(12);
        table.setAlignment(Pos.TOP_CENTER);

        double[] widths = {200, 80};
        for (double width : widths) {
            ColumnConstraints column = new ColumnConstraints();
            column.setMinWidth(width);
            column.setPrefWidth(width);
            column.setMaxWidth(width);
            table.getColumnConstraints().add(column);
        }
    }

    private void setupEmptyLabel() {
        emptyLabel.setFont(Font.font("Consolas", FontWeight.NORMAL, 22));
        emptyLabel.setStyle("-fx-text-fill: rgba(230, 215, 190, 0.8);");
    }

    private void wireHover() {
        setOnMouseEntered(event -> animateExpanded(true));
        setOnMouseExited(event -> animateExpanded(false));
    }

    private void animateExpanded(boolean expanded) {
        if (sizeAnimation != null) {
            sizeAnimation.stop();
        }
        if (contentAnimation != null) {
            contentAnimation.stop();
        }

        double targetWidth = expanded ? EXPANDED_WIDTH : COLLAPSED_WIDTH;
        double targetHeight = expanded ? EXPANDED_HEIGHT : COLLAPSED_HEIGHT;
        sizeAnimation = new Timeline(
                new KeyFrame(
                        animationDuration(0.28),
                        new KeyValue(prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(prefHeightProperty(), targetHeight, Interpolator.EASE_BOTH)
                )
        );

        FadeTransition collapsedFade = new FadeTransition(animationDuration(0.16), collapsedTitleLabel);
        collapsedFade.setToValue(expanded ? 0.0 : 1.0);

        FadeTransition expandedFade = new FadeTransition(animationDuration(0.18), expandedContent);
        expandedFade.setToValue(expanded ? 1.0 : 0.0);

        contentAnimation = new ParallelTransition(collapsedFade, expandedFade);

        sizeAnimation.play();
        contentAnimation.play();
    }

    private void refreshTable() {
        table.getChildren().clear();
        table.getRowConstraints().clear();

        for (int i = 0; i < entries.size() && i < 9; i++) {
            LeaderboardEntry entry = entries.get(i);
            int row = i;

            addValueCell((i + 1) + ". " + entry.getFormattedDate(), 0, row, Pos.CENTER_LEFT);
            addScoreCell(String.valueOf(entry.getScore()), 1, row);

            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(34);
            rowConstraints.setPrefHeight(34);
            table.getRowConstraints().add(rowConstraints);
        }

        emptyLabel.setVisible(entries.isEmpty());
        emptyLabel.setManaged(entries.isEmpty());
    }

    private void addValueCell(String text, int col, int row, Pos alignment) {
        Label label = new Label(text);
        label.setFont(Font.font("Consolas", FontWeight.NORMAL, 22));
        label.setStyle("-fx-text-fill: #e6d7be;");
        label.setAlignment(alignment);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setTextOverrun(OverrunStyle.CLIP);
        label.setWrapText(false);
        table.add(label, col, row);
    }

    private void addScoreCell(String text, int col, int row) {
        Label label = new Label(text);
        label.setFont(Font.font("Consolas", FontWeight.NORMAL, 22));
        label.setStyle("-fx-text-fill: #e6d7be;");
        label.setUnderline(true);
        label.setAlignment(Pos.CENTER);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setTextOverrun(OverrunStyle.CLIP);
        label.setWrapText(false);
        table.add(label, col, row);
    }

    private Duration animationDuration(double multiplier) {
        return Duration.millis(baseAnimationMillis * multiplier);
    }
}