package com.unicap.sin2022b.tictactoeunicap20241.Service;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unicap.sin2022b.tictactoeunicap20241.Service.Users;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

    private static final String TAG = "GameManager";
    private static GameManager instance;

    private final String[][] boardState = new String[3][3];
    private DatabaseReference gameRef;
    private FirebaseFirestore firestore;
    private boolean playerOneTurn;
    private boolean gameEnded = false;
    private String playerOneNameStr, playerTwoNameStr, playerOneProfile, playerTwoProfile;
    private GameManagerCallback callback;

    private GameManager() {
        firestore = FirebaseFirestore.getInstance();
    }

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void initializeGame(String gameId, GameManagerCallback callback) {
        this.callback = callback;
        gameRef = FirebaseDatabase.getInstance().getReference("games").child(gameId);
        loadGameState();
    }

    private void loadGameState() {
        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    playerOneNameStr = dataSnapshot.child("playerOne").child("name").getValue(String.class);
                    playerOneProfile = dataSnapshot.child("playerOne").child("profile").getValue(String.class);
                    playerTwoNameStr = dataSnapshot.child("playerTwo").child("name").getValue(String.class);
                    playerTwoProfile = dataSnapshot.child("playerTwo").child("profile").getValue(String.class);
                    Boolean turn = dataSnapshot.child("playerOneTurn").getValue(Boolean.class);
                    playerOneTurn = (turn != null) ? turn : true;

                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            boardState[i][j] = dataSnapshot.child("boardState").child(String.valueOf(i)).child(String.valueOf(j)).getValue(String.class);
                        }
                    }

                    Boolean gameEndedSnapshot = dataSnapshot.child("gameEnded").getValue(Boolean.class);
                    if (gameEndedSnapshot != null && gameEndedSnapshot) {
                        String winner = dataSnapshot.child("winner").getValue(String.class);
                        if (winner != null && !gameEnded) {
                            gameEnded = true;
                            if (callback != null) {
                                callback.onGameEnd(winner);
                            }
                        }
                    }

                    if (callback != null) {
                        callback.onGameStateLoaded();
                    }

                } else {
                    Log.e(TAG, "Data snapshot does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading game state", error.toException());
            }
        });
    }

    public String[][] getBoardState() {
        return boardState;
    }

    public boolean isPlayerOneTurn() {
        return playerOneTurn;
    }

    public String getPlayerOneName() {
        return playerOneNameStr;
    }

    public String getPlayerTwoName() {
        return playerTwoNameStr;
    }

    public String getPlayerOneProfile() {
        return playerOneProfile;
    }

    public String getPlayerTwoProfile() {
        return playerTwoProfile;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void makeMove(int row, int col) {
        if (boardState[row][col] == null) {
            if (playerOneTurn && Users.getInstance().getName().equals(playerOneNameStr)) {
                boardState[row][col] = "X";
                playerOneTurn = false;
            } else if (!playerOneTurn && Users.getInstance().getName().equals(playerTwoNameStr)) {
                boardState[row][col] = "O";
                playerOneTurn = true;
            }

            gameRef.child("boardState").child(String.valueOf(row)).child(String.valueOf(col)).setValue(boardState[row][col]);
            gameRef.child("playerOneTurn").setValue(playerOneTurn);

            String winner = checkWinner();
            if (winner != null && !gameEnded) {
                gameEnded = true;
                gameRef.child("gameEnded").setValue(true);
                if(winner.equals("X")){
                    gameRef.child("winner").setValue(playerOneNameStr);
                }
                else if(winner.equals("O")){
                    gameRef.child("winner").setValue(playerOneNameStr);
                }
                if (callback != null) {
                    callback.onGameEnd(winner + " wins!");
                }
            } else if (isBoardFull() && !gameEnded) {
                gameEnded = true;
                gameRef.child("gameEnded").setValue(true);
                gameRef.child("winner").setValue("It's a draw!");
                if (callback != null) {
                    callback.onGameEnd("It's a draw!");
                }
            }
        }
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (boardState[i][j] == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public String checkWinner() {
        String[][] combinations = {
                {boardState[0][0], boardState[0][1], boardState[0][2]},
                {boardState[1][0], boardState[1][1], boardState[1][2]},
                {boardState[2][0], boardState[2][1], boardState[2][2]},
                {boardState[0][0], boardState[1][0], boardState[2][0]},
                {boardState[0][1], boardState[1][1], boardState[2][1]},
                {boardState[0][2], boardState[1][2], boardState[2][2]},
                {boardState[0][0], boardState[1][1], boardState[2][2]},
                {boardState[0][2], boardState[1][1], boardState[2][0]}
        };

        for (String[] combination : combinations) {
            if (combination[0] != null && combination[0].equals(combination[1]) && combination[0].equals(combination[2])) {
                return combination[0];
            }
        }
        return null;

    }

    public void saveGameDataToFirestore() {
        Map<String, Object> gameData = new HashMap<>();
        gameData.put("playerOneName", playerOneNameStr);
        gameData.put("playerTwoName", playerTwoNameStr);
        gameData.put("winner", checkWinner());
        gameData.put("draw", isBoardFull() && checkWinner() == null);

        firestore.collection("finishedGames").add(gameData)
                .addOnCompleteListener(task -> gameRef.removeValue());
    }

    public interface GameManagerCallback {
        void onGameStateLoaded();
        void onGameEnd(String message);
    }
}
