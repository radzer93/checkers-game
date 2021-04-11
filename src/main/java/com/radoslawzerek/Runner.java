package com.radoslawzerek;

/**
 * Author: Radosław Żerek
 */

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class Runner extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Board board = new Board(new BoardRow());

        board.populate();

        primaryStage.setScene(new Scene(board.getGui().getGridPane(), 1014, 520, Color.DARKBLUE));
        primaryStage.setTitle("---Checkers GAME---");
        primaryStage.getIcons().add(new Image("file:src/main/resources/blackQueen.jpg"));
        primaryStage.setResizable(false);
        primaryStage.show();

        board.show(board.getGui().getGridBoard());
        board.getGui().getGameText().setText("You're playing with white pawn");
        mouseCheck(board);
    }

    private static void mouseCheck(Board board) {
        board.getGui().getGridBoard().setOnMouseClicked(event -> {
            int x = (int) (event.getX() / 50);
            int y = (int) (event.getY() / 50);
            doClick(x, y, board);
        });
    }

    private static void doClick(int x, int y, Board board) {
        if (board.getOldX() == -1) {
            board.setOldX(x);
            board.setOldY(y);
            markField(board, board.getOldX(), board.getOldY());
        } else {
            int oldX = board.getOldX();
            int oldY = board.getOldY();
            board.setOldX(-1);
            board.setOldY(-1);
            unmarkFields(board);
            if (board.movePawn(oldX, oldY, x, y, board)) {
                board.show(board.getGui().getGridBoard());
                doNextMove(board);
            } else {
                board.show(board.getGui().getGridBoard());
                mouseCheck(board);
            }
        }
    }

    public static void markField(Board board, int x, int y) {
        Figure currentFigure = board.getFigure(x, y);
        currentFigure.setChecked(true);
        board.show(board.getGui().getGridBoard());
    }

    public static void unmarkFields(Board board) {
        List<Figure> currentRow;

        for (int n = 0; n < 8; n++) {
            currentRow = board.getRows().get(n).getColumns();
            for (Figure currentFigure : currentRow) {
                currentFigure.setChecked(false);
            }
        }
        board.show(board.getGui().getGridBoard());
    }

    public static void doNextMove(Board board) {
        if (board.checkGameOver(board.moveNext))
            gameOver(board);
        board.getGui().getGameText().setText(board.getGui().getGameText().getText() + "\nNext move: "
                + board.moveNext.toString());
        if (board.moveNext == PawnColor.WHITE)
            mouseCheck(board);
        else
            ComputerMove(board);
    }

    private static void ComputerMove(Board board) {
        board.getGui().getGridBoard().setOnMouseClicked(null);
        // Player won't be possible to do moves during computer's move
        board.getGui().getGameText().setText("Next move: " +
                board.moveNext.toString() + "\nPlease wait...");
        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> {
            List<BoardRow> bestBoardRow = board.getComputerAI().getBestBoardRow(board.getRows(), board);
            board.setRows(bestBoardRow);
            board.show(board.getGui().getGridBoard());
            board.moveNext = PawnColor.WHITE;
            if (board.checkGameOver(board.moveNext))
                gameOver(board);
            board.getGui().getGameText().setText(AI.getNumberOfCombinations() +
                    " combinations analysed.\nComputer moved.\nNext move: " + board.moveNext.toString());
            mouseCheck(board);
        });
        new Thread(sleeper).start();
    }

    private static void gameOver(Board board) {
        if (board.moveNext == PawnColor.WHITE) {
            board.getRanking().setBlackWins();
            board.getGui().getGameText().setText("Black won!");
        } else if (board.moveNext == PawnColor.BLACK) {
            board.getRanking().setWhiteWins();
            board.getGui().getGameText().setText("White won!");
            board.getGui().gameOver(board);
        } else {
            board.getRanking().setDraws();
            board.getGui().getGameText().setText("Draw!");
            board.getGui().gameOver(board);
        }
    }
}
