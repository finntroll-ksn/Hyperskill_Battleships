package battleship;

import java.util.*;

class Board {
    private final String[][] gameBoard;
    private final String[][] enemyBoard;

    private final int boardSize;
    private int ships = 5;

    private static final String FREE_CELL = "~";
    private static final String SHIP_CELL = "O";
    private static final String MISS_CELL = "M";
    private static final String HIT_CELL = "X";

    private final Map<String, Set<Cell>> shipsArray = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);

    Board(int size) {
        this.boardSize = size;
        gameBoard = new String[boardSize][boardSize];
        enemyBoard = new String[boardSize][boardSize];
        fillEmptyBoard(gameBoard);
        fillEmptyBoard(enemyBoard);
    }

    private void fillEmptyBoard(String[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                board[i][j] = FREE_CELL;
            }
        }
    }

    String[][] getBoard() {
        return gameBoard;
    }

    String[][] getEnemyBoard() {
        return enemyBoard;
    }

    void printField(String[][] board) {
        System.out.println("\n  1 2 3 4 5 6 7 8 9 10");
        char rowChar = 'A';

        for (String[] line : board) {
            System.out.printf("%c %s\n", rowChar, String.join(" ", line));
            rowChar++;
        }

        System.out.println();
    }

    void placeShip(String shipName, int shipSize) {
        System.out.printf("Enter the coordinates of the %s (%d cells)%n", shipName, shipSize);

        String[] coordinates = scanner.nextLine().split("\\s+");

        checkAndPlaceShip(coordinates[0], coordinates[1], shipName, shipSize);
    }

    private void checkAndPlaceShip(String firstCoordinate, String secondCoordinate, String shipName, int shipSize) {
        Cell first = getCellCoordinates(firstCoordinate);
        Cell second = getCellCoordinates(secondCoordinate);

        if (first.getX() > second.getX() || first.getY() > second.getY()) {
            Cell tmp = second;
            second = first;
            first = tmp;
        }

        if (!checkPlacement(first, second, shipName, shipSize)) {
            return;
        }

        boolean isHorizontal = isHorizontalPlacement(first, second);
        drawShipOnBoard(first, second, isHorizontal, shipName);
        printField(gameBoard);
    }

    private void drawShipOnBoard(Cell firstPoint, Cell secondPoint, boolean isShipHorizontal, String shipName) {
        shipsArray.putIfAbsent(shipName, new HashSet<>());

        if (isShipHorizontal) {
            for (int i = firstPoint.getX(); i <= secondPoint.getX(); i++) {
                gameBoard[firstPoint.getY()][i] = SHIP_CELL;
                shipsArray.get(shipName).add(new Cell(i, firstPoint.getY()));
            }
        } else {
            for (int i = firstPoint.getY(); i <= secondPoint.getY(); i++) {
                gameBoard[i][firstPoint.getX()] = SHIP_CELL;
                shipsArray.get(shipName).add(new Cell(firstPoint.getX(), i));
            }
        }
    }

    private boolean checkPlacement(Cell firstCell, Cell secondCell, String shipName, int shipSize) {
        if (checkShipSize(firstCell, secondCell, shipSize)) {
            System.out.printf("Error! Wrong length of the %s! Try again:%n", shipName);
            this.placeShip(shipName, shipSize);

            return false;
        } else if (firstCell.getX() != secondCell.getX() && firstCell.getY() != secondCell.getY()) {
            System.out.printf("Error! Wrong ship location! Try again:%n");
            this.placeShip(shipName, shipSize);

            return false;
        } else if (!checkBorders(firstCell, secondCell)) {
            System.out.println("Error! You placed it too close to another one. Try again:");
            this.placeShip(shipName, shipSize);

            return false;
        }

        return true;
    }

    private boolean checkShipSize(Cell firstPoint, Cell secondPoint, int size) {
        return ((secondPoint.getX() - firstPoint.getX() > size - 1) || (secondPoint.getY() - firstPoint.getY() > size - 1))
                || ((secondPoint.getX() - firstPoint.getX() < size - 1) && (secondPoint.getY() - firstPoint.getY() < size - 1));
    }

    private boolean checkBorders(Cell firstPoint, Cell secondPoint) {
        Cell leftUpperCorner = getLeftUpperCornerOfCheckingArea(firstPoint);
        Cell bottomRightCorner = getBottomRightCornerOfCheckingArea(secondPoint);

        for (int i = leftUpperCorner.getY(); i <= bottomRightCorner.getY(); i++) {
            for (int j = leftUpperCorner.getX(); j <= bottomRightCorner.getX(); j++) {
                if (!gameBoard[i][j].equals(FREE_CELL)) {
                    return false;
                }
            }
        }

        return true;
    }

    private Cell getBottomRightCornerOfCheckingArea(Cell point) {
        int bottomRightCornerX = point.getX() == boardSize - 1 ? point.getX() : point.getX() + 1;
        int bottomRightCornerY = point.getY() == boardSize - 1 ? point.getX() : point.getY() + 1;

        return new Cell(bottomRightCornerX, bottomRightCornerY);
    }

    private Cell getLeftUpperCornerOfCheckingArea(Cell point) {
        int leftUpperCornerX = point.getX() == 0 ? 0 : point.getX() - 1;
        int leftUpperCornerY = point.getY() == 0 ? 0 : point.getY() - 1;

        return new Cell(leftUpperCornerX, leftUpperCornerY);
    }

    private boolean isHorizontalPlacement(Cell firstPoint, Cell secondPoint) {
        return firstPoint.getY() == secondPoint.getY();
    }

    private Cell getCellCoordinates(String coordinate) {
        int x = Integer.parseInt(coordinate.substring(1)) - 1;
        int y = Integer.parseInt(String.valueOf(coordinate.substring(0, 1).toCharArray()[0] - 'A'));

        return new Cell(x, y);
    }

    void takeAShot(Board enemyBoard) {
        String coordinate = scanner.nextLine();

        if (!checkShotCoordinate(coordinate, enemyBoard)) {
            return;
        }

        Cell shot = getCellCoordinates(coordinate);

        placeShotOnBoard(shot, enemyBoard);
    }

    private void placeShotOnBoard(Cell shot, Board board) {
        if (board.gameBoard[shot.getY()][shot.getX()].equals(FREE_CELL)) {
            board.gameBoard[shot.getY()][shot.getX()] = MISS_CELL;
            enemyBoard[shot.getY()][shot.getX()] = MISS_CELL;
//            printField(enemyBoard);
            System.out.println("You missed!");
        } else {
            board.gameBoard[shot.getY()][shot.getX()] = HIT_CELL;
            enemyBoard[shot.getY()][shot.getX()] = HIT_CELL;
//            printField(enemyBoard);

            if (!sankShip(shot, board)) {
                System.out.println("You hit a ship!");
            }
        }
    }

    private boolean checkShotCoordinate(String coordinate, Board board) {
        if (Integer.parseInt(String.valueOf(coordinate.substring(0, 1).toCharArray()[0] - 'A')) > 10 || Integer.parseInt(coordinate.substring(1)) > 10) {
            System.out.println("Error! You entered the wrong coordinates! Try again:");
            takeAShot(board);
            return false;
        }

        return true;
    }

    private boolean sankShip(Cell shot, Board board) {
        board.shipsArray.values().forEach(cell -> cell.remove(shot));
        var iterator = board.shipsArray.keySet().iterator();

        while (iterator.hasNext()) {
            var ship = iterator.next();

            if (board.shipsArray.get(ship).isEmpty()) {
                System.out.println("You sank a ship! Specify a new target:");
                iterator.remove();
                ships--;

                return true;
            }
        }

        return false;
    }

    boolean shipsAlive() {
        return ships == 0;
    }
}
