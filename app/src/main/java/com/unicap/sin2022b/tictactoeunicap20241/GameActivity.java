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
import com.unicap.sin2022b.tictactoeunicap20241.Service.Game;
import com.unicap.sin2022b.tictactoeunicap20241.Service.Users;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";

    private ImageView playerOneImage, playerTwoImage;
    private TextView playerOneName, playerTwoName;
    private ImageView[] board;

    private DatabaseReference gameRef;
    private String gameId;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        playerOneImage = findViewById(R.id.playerOneImage);
        playerTwoImage = findViewById(R.id.playerTwoImage);
        playerOneName = findViewById(R.id.playerOneName);
        playerTwoName = findViewById(R.id.playerTwoName);

        board = new ImageView[9];
        board[0] = findViewById(R.id.image1);
        board[1] = findViewById(R.id.image2);
        board[2] = findViewById(R.id.image3);
        board[3] = findViewById(R.id.image4);
        board[4] = findViewById(R.id.image5);
        board[5] = findViewById(R.id.image6);
        board[6] = findViewById(R.id.image7);
        board[7] = findViewById(R.id.image8);
        board[8] = findViewById(R.id.image9);

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
        gameRef.child("gameState").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                game = dataSnapshot.getValue(Game.class);
                if (game != null) {
                    updateUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading game state", error.toException());
            }
        });
    }

    private void updateUI() {
        if (game != null) {
            playerOneName.setText(game.getPlayerOne().getName());
            Glide.with(GameActivity.this)
                    .load(game.getPlayerOne().getProfile())
                    .transform(new CircleCrop())
                    .into(playerOneImage);

            playerTwoName.setText(game.getPlayerTwo().getName());
            Glide.with(GameActivity.this)
                    .load(game.getPlayerTwo().getProfile())
                    .transform(new CircleCrop())
                    .into(playerTwoImage);

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    updateBoardCell(i * 3 + j, game.getBoard()[i][j]);
                }
            }

            // Check for winner
            String winner = game.checkWinner();
            if (winner != null) {
                showResultDialog(winner + " wins!");
            } else if (game.isBoardFull()) {
                showResultDialog("It's a draw!");
            }
        }
    }

    private void setupBoard() {
        for (int i = 0; i < board.length; i++) {
            final int index = i;
            board[i].setOnClickListener(v -> makeMove(index));
        }
    }

    private void makeMove(int index) {
        if (game == null) return;

        int row = index / 3;
        int col = index % 3;

        if (game.isPlayerOneTurn() && Users.getInstance().getName().equals(game.getPlayerOne().getName())) {
            if (game.makeMove(row, col)) {
                gameRef.child("gameState").setValue(game);
            }
        } else if (game.isPlayerTwoTurn() && Users.getInstance().getName().equals(game.getPlayerTwo().getName())) {
            if (game.makeMove(row, col)) {
                gameRef.child("gameState").setValue(game);
            }
        }
    }

    private void updateBoardCell(int index, String value) {
        if ("X".equals(value)) {
            board[index].setImageResource(R.drawable.ic_xicon);
        } else if ("O".equals(value)) {
            board[index].setImageResource(R.drawable.ic_oicon);
        } else {
            board[index].setImageResource(0); // Limpa a célula
        }
    }

    private void showResultDialog(String message) {
        ResultDialog resultDialog = new ResultDialog(this, message);
        resultDialog.show();
    }

    public void restartGame() {
        game.startNewGame(game.getPlayerOne(), game.getPlayerTwo());
        gameRef.child("gameState").setValue(game);
        updateUI();
    }
}
