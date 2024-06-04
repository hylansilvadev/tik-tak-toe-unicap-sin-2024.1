package com.unicap.sin2022b.tictactoeunicap20241;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.unicap.sin2022b.tictactoeunicap20241.Service.GameManager;
import com.unicap.sin2022b.tictactoeunicap20241.Service.GameManager.GameManagerCallback;

public class GameActivity extends AppCompatActivity implements GameManagerCallback {

    private static final String TAG = "GameActivity";

    private static final int[] BOX_IDS = {
            R.id.image1, R.id.image2, R.id.image3,
            R.id.image4, R.id.image5, R.id.image6,
            R.id.image7, R.id.image8, R.id.image9
    };

    private View playerOneLayout;
    private View playerTwoLayout;
    private ImageView playerOneImage, playerTwoImage;
    private TextView playerOneName, playerTwoName;
    private final ImageView[] board = new ImageView[9];

    private GameManager gameManager;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameManager = GameManager.getInstance();

        playerOneImage = findViewById(R.id.playerOneImage);
        playerTwoImage = findViewById(R.id.playerTwoImage);
        playerOneLayout = findViewById(R.id.playerOneLayout);
        playerTwoLayout = findViewById(R.id.playerTwoLayout);
        playerOneName = findViewById(R.id.playerOneName);
        playerTwoName = findViewById(R.id.playerTwoName);

        for (int i = 0; i < BOX_IDS.length; i++) {
            board[i] = findViewById(BOX_IDS[i]);
        }

        Intent intent = getIntent();
        String gameId = intent.getStringExtra("gameId");
        if (gameId == null) {
            Log.e(TAG, "Game ID is null, finishing activity");
            finish();
            return;
        }
        gameManager.initializeGame(gameId, this);

        setupBoard();
    }

    @Override
    public void onGameStateLoaded() {
        updateUI();
    }

    @Override
    public void onGameEnd(String message) {
        runOnUiThread(() -> showLocalResultDialog(message));
    }

    private void updateUI() {
        playerOneName.setText(gameManager.getPlayerOneName());
        Glide.with(GameActivity.this)
                .load(gameManager.getPlayerOneProfile())
                .transform(new CircleCrop())
                .into(playerOneImage);

        playerTwoName.setText(gameManager.getPlayerTwoName());
        Glide.with(GameActivity.this)
                .load(gameManager.getPlayerTwoProfile())
                .transform(new CircleCrop())
                .into(playerTwoImage);

        String[][] boardState = gameManager.getBoardState();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                updateBoardCell(i * 3 + j, boardState[i][j]);
            }
        }

        applyPlayerTurn();
    }

    private void setupBoard() {
        for (int i = 0; i < board.length; i++) {
            final int index = i;
            board[i].setOnClickListener(v -> makeMove(index));
        }
    }

    private void makeMove(int index) {
        if (gameManager.isGameEnded()) return;

        int row = index / 3;
        int col = index % 3;

        gameManager.makeMove(row, col);
        updateBoardCell(index, gameManager.getBoardState()[row][col]);

        gameManager.checkWinner();
        applyPlayerTurn();
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

    private void showLocalResultDialog(String message) {
        ResultDialog resultDialog = new ResultDialog(this, message);
        resultDialog.setCancelable(false);
        resultDialog.show();
    }

    private void applyPlayerTurn() {
        if (gameManager.isPlayerOneTurn()) {
            playerOneLayout.setBackgroundResource(R.drawable.black_border);
            playerTwoLayout.setBackgroundResource(R.drawable.lavander_border);
        } else {
            playerTwoLayout.setBackgroundResource(R.drawable.black_border);
            playerOneLayout.setBackgroundResource(R.drawable.lavander_border);
        }
    }
}
