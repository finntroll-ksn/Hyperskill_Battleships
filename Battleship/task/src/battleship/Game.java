package battleship;

import java.util.Scanner;

class Game {
    private final Player player = new Player(new Board(10));
    private final Player opponentPlayer = new Player(new Board(10));
    private final String TURN_CHANGE_MESSAGE = "Press Enter and pass the move to another player";
    private final Scanner scanner = new Scanner(System.in);

    void initBattlefields() {
        System.out.println("Player 1, place your ships on the game field");
        player.placeShips();
        System.out.println(TURN_CHANGE_MESSAGE);
        scanner.nextLine();

        System.out.println("Player 2, place your ships on the game field");
        opponentPlayer.placeShips();
        System.out.println(TURN_CHANGE_MESSAGE);
        scanner.nextLine();
    }

    void start() {
        Player currentPlayer = player;
        Player otherPlayer = opponentPlayer;

        boolean isGameFinished = player.allShipsDestroyed() && opponentPlayer.allShipsDestroyed();

        while (!isGameFinished) {
            if (currentPlayer == player) {
                System.out.println("Player 1, it's your turn:");
            } else {
                System.out.println("Player 2, it's your turn:");
            }

            currentPlayer.takeAShot(otherPlayer);

            isGameFinished = player.allShipsDestroyed() || opponentPlayer.allShipsDestroyed();

            if (isGameFinished) {
                break;
            }

            currentPlayer = currentPlayer == player ? opponentPlayer : player;
            otherPlayer = otherPlayer == player ? opponentPlayer : player;
            System.out.println(TURN_CHANGE_MESSAGE);
            scanner.nextLine();
        }

        System.out.println("You sank the last ship. You won. Congratulations!");
    }
}
