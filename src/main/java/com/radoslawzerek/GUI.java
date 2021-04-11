package com.radoslawzerek;

/**
 * Author: Radosław Żerek
 */

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Optional;

public class GUI {
    private GridPane gridPane;
    private GridPane gridBoard;
    private Label gameText;

    public GridPane getGridPane() {
        return gridPane;
    }

    public GridPane getGridBoard() {
        return gridBoard;
    }

    public Label getGameText() {
        return gameText;
    }



    public GUI(Board board) {
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true,
                true, true, false);
        Image imageBack = new Image("file:src/main/resources/background2.jpg");
        BackgroundImage backgroundImage = new BackgroundImage(imageBack, BackgroundRepeat.REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);

        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER_RIGHT);
        gridPane.setPadding(new Insets(20, 200, 20, 20));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setBackground(background);

        ColumnConstraints gridPaneColumn1 = new ColumnConstraints();
        gridPaneColumn1.setHalignment(HPos.LEFT);
        gridPaneColumn1.setPrefWidth(400);
        gridPane.getColumnConstraints().add(gridPaneColumn1);

        ColumnConstraints gridPaneColumn2 = new ColumnConstraints();
        gridPaneColumn2.setHalignment(HPos.CENTER);
        gridPaneColumn2.setPrefWidth(400);
        gridPane.getColumnConstraints().add(gridPaneColumn2);

        gridBoard = new GridPane();
        gridBoard.setAlignment(Pos.CENTER);
        //creates a fade effect animation
        FadeTransition fadeGridBoard = new FadeTransition((Duration.millis(6000)), gridBoard);
        fadeGridBoard.setFromValue(0.0);
        fadeGridBoard.setToValue(1.0);

        GridPane gridText = new GridPane();
        gridText.setAlignment(Pos.TOP_CENTER);
        gridText.setPadding(new Insets(20, 20, 20, 20));
        gridText.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.NONE,
                CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        FadeTransition fadeGridText = new FadeTransition(Duration.millis(1500), gridText);
        fadeGridText.setFromValue(0.0);
        fadeGridText.setToValue(1.0);

        ColumnConstraints gridTextColumn1 = new ColumnConstraints();
        gridTextColumn1.setHalignment(HPos.CENTER);
        gridText.getColumnConstraints().add(gridTextColumn1);

        GridPane aboutGrid = new GridPane();
        aboutGrid.setAlignment(Pos.CENTER_LEFT);

        ColumnConstraints aboutGridColumn1 = new ColumnConstraints();
        aboutGridColumn1.setHalignment(HPos.CENTER);
        aboutGridColumn1.setPrefWidth(300);
        aboutGrid.getColumnConstraints().add(aboutGridColumn1);

        ColumnConstraints aboutGridColumn2 = new ColumnConstraints();
        aboutGridColumn2.setHalignment(HPos.CENTER);
        aboutGridColumn2.setPrefWidth(100);
        aboutGrid.getColumnConstraints().add(aboutGridColumn2);

        RowConstraints aboutGridRow = new RowConstraints();
        aboutGridRow.setValignment(VPos.CENTER);
        aboutGridRow.setPrefHeight(50);
        aboutGrid.getRowConstraints().add(aboutGridRow);

        GridPane gridButton = new GridPane();
        gridButton.setAlignment(Pos.TOP_LEFT);
        gridButton.setHgap(10);

        Text subtitle = new Text("---Checkers GAME---");
        //Defines one element of the ramp of colors to use on a gradient.
        Stop[] stop = new Stop[]{new Stop(0, Color.DARKORANGE),
                new Stop(1, Color.GRAY)};
        subtitle.setFill(new LinearGradient(0, 0, 0, 1,
                true, CycleMethod.NO_CYCLE, stop));
        subtitle.setFont(Font.loadFont("file:src/main/resources/Fonts/KrinkesRegularPERSONAL.ttf", 50));
        FadeTransition fadeSubtitle = new FadeTransition((Duration.millis(1500)), subtitle);
        fadeSubtitle.setFromValue(0.0);
        fadeSubtitle.setToValue(1.0);

        //This Transition plays a list of Animations in parallel.
        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeGridBoard, fadeGridText, fadeSubtitle);
        parallelTransition.play();

        Button ranking = new Button();
        ranking.setText("RANKING");
        ranking.setOnAction(event -> {
            new Ranking().showRanking();
        });
        Button restart = new Button();
        restart.setText("RESTART");
        restart.setOnAction(event -> {
            Optional<ButtonType> result = confirm("You're about to restart this game.");
            if (result.get() == ButtonType.OK) {
                board.populate();
                board.show(board.getGui().getGridBoard());
                board.getGui().getGameText().setText("Let's play again.\nYou're still playing with white figures.");
            }
        });
        Button quit = new Button();
        quit.setText("QUIT");
        quit.setOnAction((e) -> {
            Optional<ButtonType> result = confirm("You are about to quit.");
            if (result.get() == ButtonType.OK) {
                System.exit(0);
            }
        });
        gameText = new Label("");
        gameText.setTextFill(Color.GOLD);
        gameText.setWrapText(true);
        gameText.setFont(Font.loadFont("file:src/main/resources/Fonts/Afterglow-Regular.ttf", 24));
        gridPane.add(subtitle, 0, 0, 2, 1);
        gridPane.add(aboutGrid, 0, 1, 1, 1);
        gridPane.add(gridButton, 1, 1, 1, 1);
        gridPane.add(gridBoard, 0, 2, 1, 1);
        gridPane.add(gridText, 1, 2, 1, 1);
        gridButton.add(restart, 0, 0, 1, 1);
        gridButton.add(quit, 1, 0, 1, 1);
        gridButton.add(ranking, 4, 0, 1 ,1);
        gridText.add(gameText, 0, 1, 1, 1);
    }

    private Optional<ButtonType> confirm(String headerText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("You are just closing the program");
        alert.setHeaderText(headerText);
        alert.setContentText("Are you sure?");

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("file:src/main/resources/blackQueen.jpg"));

        return alert.showAndWait();
    }

    private Optional<ButtonType> confirmRestart() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game over");
        alert.setHeaderText(null);
        alert.setContentText("Let's play again.");

        return alert.showAndWait();
    }

    public void gameOver(Board board) {
        Optional<ButtonType> result = confirmRestart();
        if (result.get() == ButtonType.OK) {
            board.populate();
            board.show(board.getGui().getGridBoard());
            board.moveNext = PawnColor.WHITE;
            board.getGui().getGameText().setText("Let's play again.\nYou're still playing with white figures.");
        }
    }
}

