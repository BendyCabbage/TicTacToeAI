import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
    private Integer gameBoardSize = 3;
    private Integer winNumber = 3;

    private Integer playerTurn = 1;
    private Integer turnNumber = 1;

    private char player1Char = 'X';
    private char player2Char = 'O';

    private Integer[][] gameBoard = new Integer[gameBoardSize][gameBoardSize];

    public static void main(String[] args) {
        Game game = new Game();

        Scanner scanner = new Scanner(System.in);
        System.out.printf("Type 1 to play against another player or 2 for an AI: ");
        if (scanner.nextLine().equals("1")) {
            game.playGameAgainstPlayer();
        } else {
            game.playGameAgainstAI();
        }
    }

    public void playGameAgainstPlayer() {
        Scanner scanner = new Scanner(System.in);

        while (!isFinished()) {
            System.out.printf("Player %d enter your move (row col): ", playerTurn);

            String move = scanner.nextLine();
            Integer row = Integer.parseInt(move.split(" ")[0]);
            Integer col = Integer.parseInt(move.split(" ")[1]);

            playMove(new Move(row, col));
            System.out.println(this);
        }

        scanner.close();
    }

    public void playGameAgainstAI() {
        Scanner scanner = new Scanner(System.in);

        System.out.printf("Type 1 to play as player 1 or 2 to play as player 2: ");
        Integer playerNumber = Integer.parseInt(scanner.nextLine()) - 1;

        while (!isFinished()) {
            if ((turnNumber + 1) % 2 == playerNumber) {
                System.out.printf("Enter your move (row col): ");

                String move = scanner.nextLine();
                Integer row = Integer.parseInt(move.split(" ")[0]);
                Integer col = Integer.parseInt(move.split(" ")[1]);

                playMove(new Move(row, col));
            } else {
                MoveEval moveEval = miniMax(this, 10 - turnNumber, Integer.MIN_VALUE, Integer.MAX_VALUE);
                System.out.printf("AI Evaluation: %d, playing move: %s\n", moveEval.getEval(), moveEval.getMove());

                playMove(moveEval.getMove());
            }

            System.out.println(this);
        }

        scanner.close();
    }

    public Integer getGameBoardSize() {
        return gameBoardSize;
    }

    public Integer getWinNumber() {
        return winNumber;
    }

    public Integer getPlayerTurn() {
        return playerTurn;
    }

    public Integer getTurnNumber() {
        return turnNumber;
    }

    public Integer[][] getGameBoard() {
        return gameBoard;
    }

    public boolean isFinished() {
        return isBoardFull() || evaluatePosition(this) != 0;
    }

    public boolean isBoardFull() {
        return turnNumber > 9;
    }

    public void changeTurn() {
        if (playerTurn == 1) {
            playerTurn = 2;
        } else {
            playerTurn = 1;
        }
    }

    public char printSquare(Integer squareValue) {
        if (squareValue == null) {
            return ' ';
        } else if (squareValue == 1) {
            return player1Char;
        } else if (squareValue == 2) {
            return player2Char;
        }
        return ' ';
    }

    public void playMove(Move move) {
        if (gameBoard[move.getRow()][move.getCol()] == null) {
            gameBoard[move.getRow()][move.getCol()] = playerTurn;

            changeTurn();
            turnNumber++;
        } else {
            System.out.println("Invalid move!");
        }
    }

    public void revertMove(Move move) {
        gameBoard[move.getRow()][move.getCol()] = null;
        turnNumber--;
        changeTurn();
    }

    @Override
    public String toString() {
        String gameBoardString = "";

        for (int row = 0; row < gameBoardSize; row++) {
            for (int col = 0; col < gameBoardSize - 1; col++) {
                Integer currentSquare = gameBoard[row][col];
                gameBoardString += printSquare(currentSquare) + "|";
            }
            gameBoardString += printSquare(gameBoard[row][gameBoardSize - 1]);
            gameBoardString += "\n";
            if (row < gameBoardSize - 1) {
                gameBoardString += "-----\n";
            }
        }

        return gameBoardString;
    }

    public MoveEval miniMax(Game game, Integer depth, Integer alpha, Integer beta) {
        Integer evaluation = game.evaluatePosition(game);
        if (depth == 0 || evaluation != 0) {
            return new MoveEval(null, evaluation);
        }

        if (game.playerTurn == 1) {
            Move bestMove = null;
            Integer maxEval = Integer.MIN_VALUE;

            for (Move move : possibleMoves(game)) {
                game.playMove(move);
                MoveEval eval = miniMax(game, depth - 1, alpha, beta);
                game.revertMove(move);

                if (eval.getEval() > maxEval) {
                    maxEval = eval.getEval();
                    bestMove = move;
                }

                alpha = Integer.max(alpha, eval.getEval());
                if (beta <= alpha) {
                    break;
                }
            }
            return new MoveEval(bestMove, maxEval);
        } else {
            Move bestMove = null;
            Integer minEval = Integer.MAX_VALUE;

            for (Move move : possibleMoves(game)) {
                game.playMove(move);
                MoveEval eval = miniMax(game, depth - 1, alpha, beta);
                game.revertMove(move);

                if (eval.getEval() < minEval) {
                    minEval = eval.getEval();
                    bestMove = move;
                }

                beta = Integer.max(beta, eval.getEval());
                if (beta <= alpha) {
                    break;
                }
            }
            return new MoveEval(bestMove, minEval);
        }
    }

    /*
     * Returns:
     * - 0 if the position is a draw
     * - positive infinity if the position is a win for player 1
     * - negative infinity if the position is a win for player 2
     */
    public Integer evaluatePosition(Game game) {
        Integer[][] gameBoard = game.getGameBoard();

        //Checking rows
        for (int i = 0; i < game.getGameBoardSize(); i++) {
            if (gameBoard[i][0] != null && gameBoard[i][1] == gameBoard[i][2] && gameBoard[i][2] == gameBoard[i][0]) {
                if (gameBoard[i][0] == 1) return Integer.MAX_VALUE - 1;
                else return Integer.MIN_VALUE + 1;
            }
        }
        //Checking cols
        for (int j = 0; j < game.getGameBoardSize(); j++) {
            if (gameBoard[0][j] != null && gameBoard[1][j] == gameBoard[2][j] && gameBoard[2][j] == gameBoard[0][j]) {
                if (gameBoard[0][j] == 1) return Integer.MAX_VALUE - 1;
                else return Integer.MIN_VALUE + 1;
            }
        }

        //Checking first diagonal
        if (gameBoard[0][0] != null && gameBoard[1][1] == gameBoard[0][0] && gameBoard[2][2] == gameBoard[0][0]) {
            if (gameBoard[0][0] == 1) return Integer.MAX_VALUE - 1;
            else return Integer.MIN_VALUE + 1;
        }
        //Checking second diagonal
        if (gameBoard[2][0] != null && gameBoard[1][1] == gameBoard[2][0] && gameBoard[2][0] == gameBoard[0][2]) {
            if (gameBoard[2][0] == 1) return Integer.MAX_VALUE - 1;
            else if (gameBoard[2][0] == 2) return Integer.MIN_VALUE;
        }

        return 0;
    }

    public List<Move> possibleMoves(Game game) {
        List<Move> possibleMoves = new ArrayList<>();

        Integer[][] gameBoard = game.getGameBoard();
        for (int i = 0; i < game.getGameBoardSize(); i++) {
            for (int j = 0; j < game.getGameBoardSize(); j++) {
                if (gameBoard[i][j] == null) {
                    possibleMoves.add(new Move(i, j));
                }
            }
        }
        return possibleMoves;
    }
}
