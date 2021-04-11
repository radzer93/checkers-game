package com.radoslawzerek;

/**
 * Author: Radosław Żerek
 */

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AI {
    private static  int numberOfCombinations;

    public AI() {
    }

    public static int getNumberOfCombinations() {
        return numberOfCombinations;
    }

    public List<BoardRow> getBestBoardRow(List<BoardRow> inputRows, Board board) {
        List<Move> allPossibleMovesLevel1;
        List<Move> allPossibleMovesLevel2;
        List<Move> allPossibleMovesLevel3;
        List<BoardRow> hypotheticalRow1;
        List<BoardRow> hypotheticalRow2;
        List<BoardRow> hypotheticalRow3;
        List<List<BoardRow>> hypotheticalRowsLevel1 = new ArrayList<>();
        List<List<BoardRow>> hypotheticalRowsLevel2 = new ArrayList<>();
        List<List<BoardRow>> hypotheticalRowsLevel1WithWhiteMove;
        List<List<BoardRow>> hypotheticalRowsLevel1WithoutWhiteMove = new ArrayList<>();
        List<List<BoardRow>> hypotheticalRowsLevel2WithWhiteMove;
        List<List<BoardRow>> hypotheticalRowsLevel3WithWhiteMove;
        Map<Integer,Integer> hypotheticalRowsLevel1WithoutWhiteMoveMap = new HashMap<>();
        Map<Integer,Integer> hypotheticalRowsLevel2Map = new HashMap<>();
        Map<Integer,Integer> hypotheticalRowsLevel3Map = new HashMap<>();
        int resultHypotheticalRow;
        List<Integer> resultHypotethicalRows = new ArrayList<>();
        int IndexOfBestMoveAtThirdLevel;
        int counter1 = 0;
        int counter2 = 0;
        numberOfCombinations = 0;

        allPossibleMovesLevel1 = listAllPossibleMoves(copyListOfBoardRow(inputRows), PawnColor.BLACK);
        for (Move currentMove : allPossibleMovesLevel1) {
            hypotheticalRow1 = doMoveAndBuildHypotheticalRows(currentMove, copyListOfBoardRow(inputRows));
            hypotheticalRowsLevel1WithoutWhiteMove.add(hypotheticalRow1);
            hypotheticalRowsLevel1WithWhiteMove = moveWhiteFiguresAndBuildBoard(hypotheticalRow1);
            for (List<BoardRow> currentBoardRow : hypotheticalRowsLevel1WithWhiteMove) {
                hypotheticalRowsLevel1.add(currentBoardRow);
                hypotheticalRowsLevel1WithoutWhiteMoveMap.put(counter1,counter2);
                counter1++;
                numberOfCombinations++;
            }
            counter2++;
        }

        counter1 = 0;
        counter2 = 0;
        for (List<BoardRow> currentBoardRow1 : hypotheticalRowsLevel1) {
            allPossibleMovesLevel2 = listAllPossibleMoves(copyListOfBoardRow(currentBoardRow1), PawnColor.BLACK);
            for (Move currentMove : allPossibleMovesLevel2) {
                hypotheticalRow2 = doMoveAndBuildHypotheticalRows(currentMove, copyListOfBoardRow(currentBoardRow1));
                hypotheticalRowsLevel2WithWhiteMove = moveWhiteFiguresAndBuildBoard(hypotheticalRow2);
                for (List<BoardRow> currentBoardRow2 : hypotheticalRowsLevel2WithWhiteMove) {
                    hypotheticalRowsLevel2.add(currentBoardRow2);
                    hypotheticalRowsLevel2Map.put(counter1,counter2);
                    counter1++;
                    numberOfCombinations++;
                }
            }
            counter2++;
        }

        counter1 = 0;
        counter2 = 0;
        for (List<BoardRow> currentBoardRow : hypotheticalRowsLevel2) {
            allPossibleMovesLevel3 = listAllPossibleMoves(copyListOfBoardRow(currentBoardRow), PawnColor.BLACK);
            for (Move currentMove : allPossibleMovesLevel3) {
                hypotheticalRow3 = doMoveAndBuildHypotheticalRows(currentMove, copyListOfBoardRow(currentBoardRow));
                hypotheticalRowsLevel3WithWhiteMove = moveWhiteFiguresAndBuildBoard(hypotheticalRow3);
                for (List<BoardRow> currentBoardRow2 : hypotheticalRowsLevel3WithWhiteMove) {
                    hypotheticalRowsLevel3Map.put(counter1,counter2);
                    resultHypotheticalRow = scoreHypotheticalRows(currentBoardRow2);
                    resultHypotethicalRows.add(resultHypotheticalRow);
                    counter1++;
                    numberOfCombinations++;
                }
            }
            counter2++;
        }

        if (resultHypotethicalRows.size() == 0) { // computer can't move
            board.getGui().getGameText().setText("Computer can't move.\nGame ended.");
            board.getGui().gameOver(board);
        }

        IndexOfBestMoveAtThirdLevel = chooseIndexOfBestMoveAtThirdLevel(resultHypotethicalRows);
        counter1 = hypotheticalRowsLevel3Map.get(IndexOfBestMoveAtThirdLevel);
        counter2 = hypotheticalRowsLevel2Map.get(counter1);
        counter1 = hypotheticalRowsLevel1WithoutWhiteMoveMap.get(counter2);

        return hypotheticalRowsLevel1WithoutWhiteMove.get(counter1);
    }

    private List<Move> listAllPossibleMoves(List<BoardRow> rows, PawnColor colorToCheck) {
        List<Move> allPossibleMoves = new ArrayList<>();
        List<Figure> currentRow;
        int x;

        for (int y = 0; y < 8; y++) {
            currentRow = rows.get(y).getColumns();
            x = 0;
            for (Figure currentFigure : currentRow) {
                if (currentFigure instanceof None) {
                    x++;
                    continue;
                }
                if (currentFigure.getColor() != colorToCheck) {
                    x++;
                    continue;
                }
                allPossibleMoves.addAll(listAllPossibleMovesForFigure(rows, x, y, currentFigure));
                x++;
            }
        }
        return allPossibleMoves;
    }

    private List<Move> listAllPossibleMovesForFigure(List<BoardRow> rows, int currentColumn,
                                                     int currentRow, Figure currentFigure) {
        List<Move> allPossibleMovesForFigure = new ArrayList<>();

        for (int targetRow = 0; targetRow < 8; targetRow++) {
            for (int targetColumn = 0; targetColumn < 8; targetColumn++) {
                if (moveFigure(currentColumn, currentRow, targetColumn, targetRow, rows, currentFigure.getColor()))
                    allPossibleMovesForFigure.add(new Move(currentColumn, currentRow, targetColumn, targetRow));
            }
        }
        return allPossibleMovesForFigure;
    }

    private boolean moveFigure(int currentColumn, int currentRow, int targetColumn, int targetRow,
                               List<BoardRow> rows, PawnColor colorToCheck) {

        // common for every figure
        if (checkNoMove(currentColumn, currentRow, targetColumn, targetRow)) return false;
        if (checkTargetOccupied(targetColumn, targetRow, rows)) return false;

        // we're moving pawn
        if (getFigure(currentColumn, currentRow, rows) instanceof Pawn) {
            if (!tryMovePawnWithBeat(currentColumn, currentRow, targetColumn, targetRow, rows, false)) {
                return !isMovePawnWithoutBeatNotPossible(currentColumn, currentRow, targetColumn,
                        targetRow, rows, colorToCheck);
            }
        }

        // we're moving queen
        if (getFigure(currentColumn, currentRow, rows) instanceof Queen) {
            if (isMoveQueenNotDiagonal(currentColumn, currentRow, targetColumn, targetRow))
                return false;
            return moveQueen(currentColumn, currentRow, targetColumn, targetRow, rows, false);
        }

        return true;
    }

    private Figure getFigure(int targetColumn, int targetRow, List<BoardRow> rows) {
        return rows.get(targetRow).getColumns().get(targetColumn);
    }

    private void setFigure(int targetColumn, int targetRow, Figure figure, List<BoardRow> rows) {
        rows.get(targetRow).getColumns().set(targetColumn, figure);
    }

    private boolean checkNoMove(int currentColumn, int currentRow, int targetCol, int targetRow) {
        return currentColumn == targetCol && currentRow == targetRow;
    }

    private boolean checkTargetOccupied(int targetColumn, int targetRow, List<BoardRow> rows) {
        return getFigure(targetColumn, targetRow, rows).getColor() != PawnColor.NONE;
    }

    private boolean tryMovePawnWithBeat(int currentColumn, int currentRow, int targetColumn,
                                        int targetRow, List<BoardRow> rows, boolean doBeat) {
        boolean result = tryToMovePawnWithBeatInDirection(currentColumn, currentRow, targetColumn, targetRow,
                currentColumn + 2, currentRow - 2, currentColumn + 1,
                currentRow - 1, rows, doBeat);
        result = result || tryToMovePawnWithBeatInDirection(currentColumn, currentRow, targetColumn, targetRow,
                currentColumn + 2, currentRow + 2, currentColumn + 1,
                currentRow + 1, rows, doBeat);
        result = result || tryToMovePawnWithBeatInDirection(currentColumn, currentRow, targetColumn, targetRow,
                currentColumn - 2, currentRow + 2, currentColumn - 1,
                currentRow + 1, rows, doBeat);
        result = result || tryToMovePawnWithBeatInDirection(currentColumn, currentRow, targetColumn, targetRow,
                currentColumn - 2, currentRow - 2, currentColumn - 1,
                currentRow - 1, rows, doBeat);
        return result;
    }

    private boolean tryToMovePawnWithBeatInDirection(int currentColumn, int currentRow, int targetColumn, int targetRow,
                                                     int possibleColumn, int possibleRow, int columnToBeat, int rowToBeat,
                                                     List<BoardRow> rows, boolean doBeat) {
        if (possibleColumn == targetColumn && possibleRow == targetRow &&
                getFigure(columnToBeat, rowToBeat, rows).getColor() != PawnColor.NONE &&
                getFigure(columnToBeat, rowToBeat, rows).getColor() != getFigure(currentColumn, currentRow, rows).getColor()) {
            if (doBeat) {
                doMovePawnWithBeat(currentColumn, currentRow, targetColumn, targetRow, columnToBeat, rowToBeat, rows);
            }
            return true;
        }
        return false;
    }

    private void doMovePawnWithBeat(int currentColumn, int currentRow, int targetColumn, int targetRow,
                                    int toRemoveColumn, int toRemoveRow, List<BoardRow> rows) {
        setFigure(toRemoveColumn, toRemoveRow, new None(), rows);
        setFigure(targetColumn, targetRow, new Pawn(getFigure(currentColumn, currentRow, rows).getColor()), rows);
        setFigure(currentColumn, currentRow, new None(), rows);
    }

    private boolean isMovePawnWithoutBeatNotPossible(int currentColumn, int currentRow, int targetColumn, int targetRow,
                                                     List<BoardRow> rows, PawnColor colorToCheck) {
        if (!(currentColumn + 1 == targetColumn || currentColumn - 1 == targetColumn)) {
            return true;
        }
        if(colorToCheck == PawnColor.BLACK) {
            return checkWrongDirection(currentColumn, currentRow, targetRow, PawnColor.BLACK,
                    currentRow + 1, rows);
        } else {
            return checkWrongDirection(currentColumn, currentRow, targetRow, PawnColor.WHITE,
                    currentRow - 1, rows);
        }
    }

    private boolean checkWrongDirection(int currentColumn, int currentRow, int targetRow, PawnColor currentColor,
                                        int rowToTry, List<BoardRow> rows) {
        return getFigure(currentColumn, currentRow, rows).getColor() == currentColor && rowToTry != targetRow;
    }

    private boolean isMoveQueenNotDiagonal(int currentColumn, int currentRow, int targetColumn, int targetRow) {
        return !(currentColumn - targetColumn == currentRow - targetRow ||
                currentColumn - targetColumn == targetRow - currentRow ||
                targetColumn - currentColumn == currentRow - targetRow ||
                targetColumn - currentColumn == targetRow - currentRow);
    }

    private boolean moveQueen(int currentColumn, int currentRow, int targetColumn, int targetRow,
                              List<BoardRow> rows, boolean doBeat) {
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

            if (isQueenMoveBlockedByOurFigure(checkedColumn, checkedRow, rows)) return false;
            if (getFigure(checkedColumn, checkedRow, rows).getColor() != PawnColor.NONE) {
                if (checkFieldAfterOpponentFigure)
                    return false;
                checkFieldAfterOpponentFigure = true;
                continue;
            }
            if (checkFieldAfterOpponentFigure) {
                if (doBeat) {
                    setFigure(lastCheckedColumn, lastCheckedRow, new None(), rows);
                    setFigure(checkedColumn, checkedRow, new Queen(getFigure(currentColumn, currentRow, rows).getColor()), rows);
                    setFigure(currentColumn, currentRow, new None(), rows);
                }
                return true;
            }
        }
        if (doBeat)
            doMove(currentColumn, currentRow, targetColumn, targetRow, rows);
        return true;
    }

    private boolean isQueenMoveIncrease(int difference) {
        return difference <= 0;
    }

    private void CheckBecomeQueen(int targetColumn, int targetRow, List<BoardRow> rows) {
        if (targetRow == 7)
            setFigure(targetColumn, targetRow, new Queen(getFigure(targetColumn, targetRow, rows).getColor()), rows);
    }

    private int moveQueenNextIteration(int checked, boolean increase) {
        if (increase)
            checked++;
        else
            checked--;
        return checked;
    }

    private boolean isQueenMoveBlockedByOurFigure(int checkedColumn, int checkedRow, List<BoardRow> rows) {
        return getFigure(checkedColumn, checkedRow, rows).getColor() == PawnColor.BLACK;
    }

    private List<BoardRow> doMoveAndBuildHypotheticalRows(Move currentMove, List<BoardRow> rows) {
        if (getFigure(currentMove.getCurrentColumn(), currentMove.getCurrentRow(), rows) instanceof Pawn) {
            if (!tryMovePawnWithBeat(currentMove.getCurrentColumn(), currentMove.getCurrentRow(),
                    currentMove.getTargetColumn(), currentMove.getTargetRow(), rows, true)) {
                doMove(currentMove.getCurrentColumn(), currentMove.getCurrentRow(),
                        currentMove.getTargetColumn(), currentMove.getTargetRow(), rows);
            }
            CheckBecomeQueen(currentMove.getTargetColumn(), currentMove.getTargetRow(), rows);
        }
        if (getFigure(currentMove.getCurrentColumn(), currentMove.getCurrentRow(), rows) instanceof Queen) {
            moveQueen(currentMove.getCurrentColumn(), currentMove.getCurrentRow(),
                    currentMove.getTargetColumn(), currentMove.getTargetRow(), rows, true);
        }
        return rows;
    }

    private void doMove(int currentColumn, int currentRow, int targetColumn, int targetRow, List<BoardRow> rows) {
        setFigure(targetColumn, targetRow, getFigure(currentColumn, currentRow, rows), rows);
        setFigure(currentColumn, currentRow, new None(), rows);
    }

    private List<List<BoardRow>> moveWhiteFiguresAndBuildBoard(List<BoardRow> inputBoard) {
        List<List<BoardRow>> ListOfBoardsWithWhiteMove = new ArrayList<>();
        List<Move> allPossibleMovesWhite;
        List<BoardRow> hypotheticalRowWhite;

        allPossibleMovesWhite = listAllPossibleMoves(copyListOfBoardRow(inputBoard), PawnColor.WHITE);
        for (Move currentMove : allPossibleMovesWhite) {
            hypotheticalRowWhite = doMoveAndBuildHypotheticalRows(currentMove, copyListOfBoardRow(inputBoard));
            ListOfBoardsWithWhiteMove.add(hypotheticalRowWhite);
        }
        return ListOfBoardsWithWhiteMove;
    }

    private int scoreHypotheticalRows(List<BoardRow> rows) {
        List<Figure> currentRow;
        int queenValue = 3;
        int score;
        int scoreComputer = 0;
        int scoreHuman = 0;

        for (int y = 0; y < 8; y++) {
            currentRow = rows.get(y).getColumns();
            for (Figure currentFigure : currentRow) {
                if (currentFigure instanceof None) {
                    continue;
                }
                if (currentFigure.getColor() == PawnColor.WHITE) {
                    if (currentFigure instanceof Queen)
                        scoreHuman = scoreHuman + queenValue;
                    else
                        scoreHuman++;
                } else {
                    if (currentFigure instanceof Queen)
                        scoreComputer = scoreComputer + queenValue;
                    else
                        scoreComputer++;
                }
            }
        }
        score = scoreComputer - scoreHuman;
        return score;
    }

    private int chooseIndexOfBestMoveAtThirdLevel(List<Integer> scoreHypotethicalRows) {
        int bestBoardRow = 0;
        boolean pickRandom = true;
        int random;

        while (pickRandom) {
            random = ThreadLocalRandom.current().nextInt(0, scoreHypotethicalRows.size());
            if (scoreHypotethicalRows.get(random).equals(Collections.max(scoreHypotethicalRows))) {
                bestBoardRow = random;
                pickRandom = false;
            }
        }
        return bestBoardRow;
    }

    private static List<BoardRow> copyListOfBoardRow(List<BoardRow> inputList) {
        List<BoardRow> outputList = new ArrayList<>();
        for (int counter = 0; counter < 8; counter++)
            outputList.add(new BoardRow());
        for (int counter1 = 0; counter1 < 8; counter1++)
            for (int counter2 = 0; counter2 < 8; counter2++) {
                Figure figure = createCopy(inputList.get(counter1).getColumns().get(counter2));
                outputList.get(counter1).setFigure(counter2, figure);
            }
        return outputList;
    }

    private static Figure createCopy(Figure figure) {
        if(figure instanceof Pawn)
            return new Pawn(figure.getColor());
        else if (figure instanceof Queen)
            return new Queen(figure.getColor());
        else
            return new None();
    }
}
