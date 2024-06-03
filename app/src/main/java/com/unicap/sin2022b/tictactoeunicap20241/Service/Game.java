package com.unicap.sin2022b.tictactoeunicap20241.Service;

public class Game {

    private static Game instance;
    private Users playerOne;
    private Users playerTwo;
    private Integer playerTurn; // 1 para Player One, 2 para Player Two
    private String[][] board; // Representação do tabuleiro 3x3

    // Estados do jogo
    private static final String EMPTY = "";
    private static final String PLAYER_ONE_MARKER = "X";
    private static final String PLAYER_TWO_MARKER = "O";

    private Game() {
        board = new String[3][3];
        resetBoard();
    }

    public static Game getInstance(){
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    // Método para iniciar um novo jogo
    public void startNewGame(Users playerOne, Users playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.playerTurn = 1; // Player One começa
        resetBoard();
    }

    // Método para resetar o tabuleiro
    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    // Método para fazer uma jogada
    public boolean makeMove(int row, int col) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3 || !board[row][col].equals(EMPTY)) {
            return false; // Jogada inválida
        }

        board[row][col] = (playerTurn == 1) ? PLAYER_ONE_MARKER : PLAYER_TWO_MARKER;
        playerTurn = (playerTurn == 1) ? 2 : 1; // Alterna o turno
        return true;
    }

    // Método para verificar o vencedor
    public String checkWinner() {
        // Verifica linhas
        for (int i = 0; i < 3; i++) {
            if (!board[i][0].equals(EMPTY) && board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2])) {
                return board[i][0];
            }
        }

        // Verifica colunas
        for (int i = 0; i < 3; i++) {
            if (!board[0][i].equals(EMPTY) && board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i])) {
                return board[0][i];
            }
        }

        // Verifica diagonais
        if (!board[0][0].equals(EMPTY) && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])) {
            return board[0][0];
        }

        if (!board[0][2].equals(EMPTY) && board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0])) {
            return board[0][2];
        }

        return null; // Nenhum vencedor
    }

    // Método para verificar se o tabuleiro está cheio (empate)
    public boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].equals(EMPTY)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Métodos getters e setters para players e playerTurn

    public Users getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(Users playerOne) {
        this.playerOne = playerOne;
    }

    public Users getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(Users playerTwo) {
        this.playerTwo = playerTwo;
    }

    public Integer getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(Integer playerTurn) {
        this.playerTurn = playerTurn;
    }

    public String[][] getBoard() {
        return board;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }

    public boolean isPlayerOneTurn() {
        return playerTurn == 1;
    }

    public boolean isPlayerTwoTurn() {
        return playerTurn == 2;
    }
}
