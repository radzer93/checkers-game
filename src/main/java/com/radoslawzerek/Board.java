package com.radoslawzerek;

/**
 * Author: Radosław Żerek
 */

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Board {
    GUI gui = new GUI(this);
    AI computerAI = new AI();
    private Ranking ranking = new Ranking();
    private List<BoardRow> rows = new ArrayList<>();
    private int oldX = -1;
    private int oldY = -1;

    public PawnColor moveNext = PawnColor.WHITE;

    public Board(BoardRow boardRow) {
        for (int i = 0; i < 8; i++) {
            rows.add(new BoardRow());
        }
    }
    public Ranking getRanking() {
        return ranking;
    }

    public GUI getGui() {
        return gui;
    }

    public AI getComputerAI() {
        return computerAI;
    }

    public int getOldX() {
        return oldX;
    }

    public int getOldY() {
        return oldY;
    }

    public void setOldX(int oldX) {
        this.oldX = oldX;
    }

    public void setOldY(int oldY) {
        this.oldY = oldY;
    }

    public List<BoardRow> getRows() {
        return rows;
    }

    public void setRows(List<BoardRow> rows) {
        this.rows = rows;
    }

    public Figure getFigure(int column, int row) {
        return rows.get(row).getColumns().get(column);
    }

    public void setFigure(int column, int row, Figure figure) {
        rows.get(row).getColumns().set(column, figure);
    }

    public void populate() {
        for (int column = 0; column < 8; column += 1) {
            for (int row = 0; row < 8; row += 1) {
                this.setFigure(column, row, new None());
            }
        }
        for (int column = 1; column < 8; column += 2) {
            this.setFigure(column, 0, new Pawn(PawnColor.BLACK));
            this.setFigure(column, 2, new Pawn(PawnColor.BLACK));
            this.setFigure(column, 6, new Pawn(PawnColor.WHITE));
        }
        for (int column = 0; column < 8; column += 2) {
            this.setFigure(column, 1, new Pawn(PawnColor.BLACK));
            this.setFigure(column, 7, new Pawn(PawnColor.WHITE));
            this.setFigure(column, 5, new Pawn(PawnColor.WHITE));
        }
    }

    public boolean movePawn(int currentColumn, int currentRow, int targetColumn,
                            int targetRow, Board board) {
        //Common for every figure
        if (checkOutOfBorders(currentColumn, currentRow, targetColumn, targetRow,
                board.getGui().getGameText())) return false;
        if (checkWrongColor(currentColumn, currentRow, board.getGui().getGameText())) return false;
        if (checkNoMove(currentColumn, currentRow, targetColumn, targetRow, board.getGui().getGameText())) return false;
        if (checkTargetOccupied(targetColumn, targetRow, board.getGui().getGameText())) return false;

        //Moving pawn
        if (getFigure(currentColumn, currentRow) instanceof Pawn) {
            if (!tryMovePawnWithBeat(currentColumn, currentRow, targetColumn,
                    targetRow, board.getGui().getGameText())) {
                if (movePawnWithoutBeatNotPossible(currentColumn, currentRow,
                        targetColumn, targetRow, board.getGui().getGameText())) {
                    return false;
                } else {
                    doMove(currentColumn, currentRow, targetColumn, targetRow, board);
                }
            }
            CheckBecomeQueen(targetColumn, targetRow);
        }

        //Moving queen
        if (getFigure(currentColumn, currentRow) instanceof Queen) {
            if (isMoveQueenNotDiagonal(currentColumn, currentRow, targetColumn, targetRow, board.getGui().getGameText()))
                return false;
            if (!moveQueen(currentColumn, currentRow, targetColumn, targetRow, board))
                return false;
        }

        switchPlayer();
        return true;
    }

    private boolean isMoveQueenNotDiagonal(int currentColumn, int currentRow,
                                           int targetColumn, int targetRow, Label gameText) {
        if (!(currentColumn - targetColumn == currentRow - targetRow ||
                currentColumn - targetColumn == targetRow - currentRow ||
                targetColumn - currentColumn == currentRow - targetRow ||
                targetColumn - currentColumn == targetRow - currentRow)) {
            gameText.setText("You can only move diagonally.");
            return true;
        }
        return false;
    }

    private boolean moveQueen(int currentColumn, int currentRow, int targetColumn, int targetRow, Board board) {
        int checkedColumn = currentColumn;
        int checkedRow = currentRow;
        int lastCheckedColumn;
        int lastCheckedRow;
        boolean checkFieldAfterOpponentFigure = false;

        boolean horizontalIncrease = isQueenMoveIncrease(currentColumn - targetColumn);
        boolean verticalIncrease = isQueenMoveIncrease(currentRow - targetRow);

        while (checkedColumn != targetColumn) {
            lastCheckedColumn = checkedColumn;
            lastCheckedRow = checkedRow;
            checkedColumn = moveQueenNextIteration(checkedColumn, horizontalIncrease);
            checkedRow = moveQueenNextIteration(checkedRow, verticalIncrease);

            if (isQueenMoveBlockedByOurFigure(checkedColumn, checkedRow, board.getGui().getGameText())) return false;
            if (getFigure(checkedColumn, checkedRow).getColor() != PawnColor.NONE) {
                if (checkFieldAfterOpponentFigure) {
                    board.getGui().getGameText().setText("There are two figures one after another on your way.");
                    return false;
                }
                checkFieldAfterOpponentFigure = true;
                continue;
            }
            if (checkFieldAfterOpponentFigure) { // we are on empty field and after opponent's figure so beat
                setFigure(lastCheckedColumn, lastCheckedRow, new None());
                setFigure(checkedColumn, checkedRow, new Queen(getFigure(currentColumn, currentRow).getColor()));
                setFigure(currentColumn, currentRow, new None());
                return true;
            }
        }
        doMove(currentColumn, currentRow, targetColumn, targetRow, board);
        return true;
    }

    private boolean isQueenMoveBlockedByOurFigure(int checkedColumn, int checkedRow, Label gameText) {
        if (getFigure(checkedColumn, checkedRow).getColor() == moveNext) {
            gameText.setText("Another of your figures is on the way.");
            return true;
        }
        return false;
    }

    private int moveQueenNextIteration(int checked, boolean increase) {
        if (increase)
            checked++;
        else
            checked--;
        return checked;
    }

    private boolean isQueenMoveIncrease(int difference) {
        return difference <= 0;
    }

    private void CheckBecomeQueen(int targetColumn, int targetRow) {
        if (targetRow == 0)
            setFigure(targetColumn, targetRow, new Queen(getFigure(targetColumn, targetRow).getColor()));
    }

    public boolean checkGameOver(PawnColor checkColor) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (rows.get(i).getColumns().get(j).getColor() == checkColor) {
                    return false;
                }
            }
        }
        return true;
    }

    private void switchPlayer() {
        if (moveNext == PawnColor.WHITE) {
            moveNext = PawnColor.BLACK;
        } else {
            moveNext = PawnColor.WHITE;
        }
    }

    private void doMove(int currentCol, int currentRow, int targetCol, int targetRow, Board board) {
        setFigure(targetCol, targetRow, getFigure(currentCol, currentRow));
        setFigure(currentCol, currentRow, new None());
        board.getGui().getGameText().setText("Move done");
    }

    private boolean movePawnWithoutBeatNotPossible(int currentColumn, int currentRow, int targetColumn,
                                                   int targetRow, Label gameText) {
        if (!(currentColumn + 1 == targetColumn || currentColumn - 1 == targetColumn)) {
            gameText.setText("Move is not diagonal.");
            return true;
        }
        return checkWrongDirection(currentColumn, currentRow, targetRow, currentRow - 1, gameText);
    }

    private boolean checkWrongDirection(int currentColumn, int currentRow, int targetRow, int rowToTry, Label gameText) {
        if (getFigure(currentColumn, currentRow).getColor() == PawnColor.WHITE && rowToTry != targetRow) {
            gameText.setText("White pawn can move only up.");
            return true;
        }
        return false;
    }

    private boolean tryMovePawnWithBeat(int currentColumn, int currentRow, int targetColumn, int targetRow, Label gameText) {
        boolean result = tryMovePawnWithBeatInDirection(currentColumn, currentRow, targetColumn, targetRow,
                currentColumn + 2, currentRow - 2, currentColumn + 1, currentRow - 1);
        result = result || tryMovePawnWithBeatInDirection(currentColumn, currentRow, targetColumn, targetRow,
                currentColumn + 2, currentRow + 2, currentColumn + 1, currentRow + 1);
        result = result || tryMovePawnWithBeatInDirection(currentColumn, currentRow, targetColumn, targetRow,
                currentColumn - 2, currentRow + 2, currentColumn - 1, currentRow + 1);
        result = result || tryMovePawnWithBeatInDirection(currentColumn, currentRow, targetColumn, targetRow,
                currentColumn - 2, currentRow - 2, currentColumn - 1, currentRow - 1);
        if (result)
            gameText.setText("Beat done.");
        return result;
    }

    private boolean tryMovePawnWithBeatInDirection(int currentColumn, int currentRow, int targetColumn, int targetRow,
                                                   int possibleColumn, int possibleRow, int columnToHit, int rowToHit) {
        if (possibleColumn == targetColumn && possibleRow == targetRow &&
                getFigure(columnToHit, rowToHit).getColor() != PawnColor.NONE &&
                getFigure(columnToHit, rowToHit).getColor() != getFigure(currentColumn, currentRow).getColor()) {
            tryMovePawnWithBeat(currentColumn, currentRow, targetColumn, targetRow, columnToHit, rowToHit);
            return true;
        }
        return false;
    }

    private void tryMovePawnWithBeat(int currentColumn, int currentRow, int targetColumn,
                                     int targetRow, int removeColumn, int removeRow) {
        setFigure(removeColumn, removeRow, new None());
        setFigure(targetColumn, targetRow, new Pawn(getFigure(currentColumn, currentRow).getColor()));
        setFigure(currentColumn, currentRow, new None());
    }

    private boolean checkTargetOccupied(int targetColumn, int targetRow, Label gameText) {
        if (getFigure(targetColumn, targetRow).getColor() != PawnColor.NONE) {
            gameText.setText("Target field is occupied by figure.");
            return true;
        }
        return false;
    }

    private boolean checkNoMove(int currentColumn, int currentRow, int targetColumn, int targetRow, Label gameText) {
        if (currentColumn == targetColumn && currentRow == targetRow) {
            gameText.setText("Current field and target field are the same.");
            return true;
        }
        return false;
    }

    private boolean checkWrongColor(int currentColumn, int currentRow, Label gameText) {
        if (getFigure(currentColumn, currentRow).getColor() == PawnColor.BLACK) {
            gameText.setText("You can't move opponent's figure.");
            return true;
        } else if (getFigure(currentColumn, currentRow).getColor() == PawnColor.NONE) {
            gameText.setText("There is no figure.");
            return true;
        }
        return false;
    }

    private boolean checkOutOfBorders(int currentColumn, int currentRow,
                                      int targetColumn, int targetRow, Label gameText) {
        if (currentColumn < 0 || currentColumn > 8 || currentRow < 0 || currentRow > 8 ||
                targetColumn < 0 || targetColumn > 8 || targetRow < 0 || targetRow > 8) {
            gameText.setText("There's no such field.");
            return true;
        }
        return false;
    }

    public void show(GridPane boardGrid) {
        List<Figure> currentRow;
        String file = "";
        int x;
        boolean emptyBlack;
        Rectangle redRectangle = new Rectangle(50, 50);
        redRectangle.setFill(Paint.valueOf("RED"));
        redRectangle.setOpacity(0.4);

        for (int y = 0; y < 8; y++) {
            currentRow = rows.get(y).getColumns();
            x = 0;
            for (Figure currentFigure : currentRow) {
                if (currentFigure instanceof None) {
                    emptyBlack = ((y == 0) && ((x == 0) || (x == 2) || (x == 4) || (x == 6)));
                    emptyBlack = emptyBlack || ((y == 1) && ((x == 1) || (x == 3) || (x == 5) || (x == 7)));
                    emptyBlack = emptyBlack || ((y == 2) && ((x == 0) || (x == 2) || (x == 4) || (x == 6)));
                    emptyBlack = emptyBlack || ((y == 3) && ((x == 1) || (x == 3) || (x == 5) || (x == 7)));
                    emptyBlack = emptyBlack || ((y == 4) && ((x == 0) || (x == 2) || (x == 4) || (x == 6)));
                    emptyBlack = emptyBlack || ((y == 5) && ((x == 1) || (x == 3) || (x == 5) || (x == 7)));
                    emptyBlack = emptyBlack || ((y == 6) && ((x == 0) || (x == 2) || (x == 4) || (x == 6)));
                    emptyBlack = emptyBlack || ((y == 7) && ((x == 1) || (x == 3) || (x == 5) || (x == 7)));
                    if (emptyBlack)
                        file = "file:src/main/resources/blueField.jpg";
                    else
                        file = "file:src/main/resources/whiteField.jpg";
                }
                if (currentFigure instanceof Pawn) {
                    if (currentFigure.getColor() == PawnColor.WHITE) {
                        file = "file:src/main/resources/whitePawn.jpg";
                    } else {
                        file = "file:src/main/resources/blackPawn.jpg";
                    }
                }
                if (currentFigure instanceof Queen) {
                    if (currentFigure.getColor() == PawnColor.WHITE) {
                        file = "file:src/main/resources/whiteQueen.jpg";
                    } else {
                        file = "file:src/main/resources/blackQueen.jpg";
                    }
                }
                if (currentFigure.getChecked()) {
                    boardGrid.add(redRectangle, x, y, 1, 1);
                } else {
                    boardGrid.add(new ImageView(new Image(file, 50, 50,
                            true, true)), x, y, 1, 1);
                }
                x++;
            }
        }
    }
}
