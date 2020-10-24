package battleship;

class Player {
    private final Board board;

    Player(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    void placeShips() {
        board.printField(board.getBoard());
        board.placeShip("Aircraft Carrier", 5);
        board.placeShip("Battleship", 4);
        board.placeShip("Submarine", 3);
        board.placeShip("Cruiser", 3);
        board.placeShip("Destroyer", 2);
    }

    void takeAShot(Player otherPlayer) {
        board.printField(board.getEnemyBoard());
        System.out.println("---------------------");
        board.printField(board.getBoard());
        board.takeAShot(otherPlayer.getBoard());
    }

    boolean allShipsDestroyed() {
        return board.shipsAlive();
    }
}
