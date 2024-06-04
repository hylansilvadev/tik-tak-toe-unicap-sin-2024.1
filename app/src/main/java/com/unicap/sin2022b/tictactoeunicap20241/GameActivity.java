package com.unicap.sin2022b.tictactoeunicap20241;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unicap.sin2022b.tictactoeunicap20241.Service.Users;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";
    private static final int[] BOX_IDS = {
            R.id.image1, R.id.image2, R.id.image3,
            R.id.image4, R.id.image5, R.id.image6,
            R.id.image7, R.id.image8, R.id.image9
    };

    private ImageView playerOneImage, playerTwoImage;
    private TextView playerOneName, playerTwoName;
    private ImageView[] board = new ImageView[9];
    private String[][] boardState = new String[3][3];

    private DatabaseReference gameRef;
    private String gameId;
    private boolean playerOneTurn;
    private String playerOneNameStr, playerTwoNameStr, playerOneProfile, playerTwoProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        playerOneImage = findViewById(R.id.playerOneImage);
        playerTwoImage = findViewById(R.id.playerTwoImage);
        playerOneName = findViewById(R.id.playerOneName);
        playerTwoName = findViewById(R.id.playerTwoName);

        for (int i = 0; i < BOX_IDS.length; i++) {
            board[i] = findViewById(BOX_IDS[i]);
        }

        Intent intent = getIntent();
        gameId = intent.getStringExtra("gameId");
        if (gameId == null) {
            Log.e(TAG, "Game ID is null, finishing activity");
            finish();
            return;
        }
        gameRef = FirebaseDatabase.getInstance().getReference("games").child(gameId);

        loadGameState();
        setupBoard();
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

                    updateUI();
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

    private void updateUI() {
        if (playerOneNameStr != null && playerTwoNameStr != null) {
            playerOneName.setText(playerOneNameStr);
            Glide.with(GameActivity.this)
                    .load(playerOneProfile)
                    .transform(new CircleCrop())
                    .into(playerOneImage);

            playerTwoName.setText(playerTwoNameStr);
            Glide.with(GameActivity.this)
                    .load(playerTwoProfile)
                    .transform(new CircleCrop())
                    .into(playerTwoImage);

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    updateBoardCell(i * 3 + j, boardState[i][j]);
                }
            }

            String winner = checkWinner();
            if (winner != null) {
                showResultDialog(winner + " wins!");
            } else if (isBoardFull()) {
                showResultDialog("It's a draw!");
            }

            applyPlayerTurn();
        }
    }

    private void setupBoard() {
        for (int i = 0; i < board.length; i++) {
            final int index = i;
            board[i].setOnClickListener(v -> makeMove(index));
        }
    }

    private void makeMove(int index) {
        if (boardState == null) return;

        int row = index / 3;
        int col = index % 3;

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
            if (winner != null) {
                showResultDialog(winner + " wins!");
            } else if (isBoardFull()) {
                showResultDialog("It's a draw!");
            }

            applyPlayerTurn();
        }
    }

    private void updateBoardCell(int index, String value) {
        if ("X".equals(value)) {
            board[index].setImageResource(R.drawable.ic_xicon);
        } else if ("O".equals(value)) {
            board[index].setImageResource(R.drawable.ic_oicon);
        } else {
            board[index].setImageResource(0); // Clear cell
        }
    }

    private void showResultDialog(String message) {
        ResultDialog resultDialog = new ResultDialog(this, message);
        resultDialog.show();
    }

    private void applyPlayerTurn() {
        if (playerOneTurn) {
            playerOneImage.setBackgroundResource(R.drawable.black_border);
            playerTwoImage.setBackgroundResource(R.drawable.lavander_border);
        } else {
            playerTwoImage.setBackgroundResource(R.drawable.black_border);
            playerOneImage.setBackgroundResource(R.drawable.lavander_border);
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

    private String checkWinner() {
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

    public void restartGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardState[i][j] = null;
            }
        }
        playerOneTurn = true;

        gameRef.child("boardState").setValue(boardState);
        gameRef.child("playerOneTurn").setValue(playerOneTurn);
        updateUI();
    }
}
