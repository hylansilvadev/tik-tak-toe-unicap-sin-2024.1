package com.unicap.sin2022b.tictactoeunicap20241;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.unicap.sin2022b.tictactoeunicap20241.Service.GameManager;

public class ResultDialog extends Dialog {

    private final String message;
    private final GameActivity gameActivity;
    private Button restartButton;
    private TextView resultText;

    private GameManager gameManager;

    public ResultDialog(GameActivity gameActivity, String message) {
        super(gameActivity);
        this.gameActivity = gameActivity;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_result);

        initViews();
        setMessage();
//        setButtonListener();
//        gameManager = GameManager.getInstance();
//        gameManager.saveGameDataToFirestore();
    }

    private void initViews() {
        resultText = findViewById(R.id.messageTV);
        restartButton = findViewById(R.id.startNewBtn);
    }

    private void setMessage() {
        resultText.setText(message);
    }

    private void setButtonListener() {
        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(gameActivity, MainMenuActivity.class);
            gameActivity.startActivity(intent);
            dismiss();
        });
    }
}
