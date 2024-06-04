package com.unicap.sin2022b.tictactoeunicap20241;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultDialog extends Dialog {

    private final String message;
    private Button restartButton;
    private TextView resultText;

    public ResultDialog(Context context, String message) {
        super(context);
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_result);

        initViews();
        setMessage();
        setButtonListener();
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
            Context context = getContext();
            if (context instanceof GameActivity) {
                Intent intent = new Intent(context, MainMenuActivity.class);
                context.startActivity(intent);
            }
            dismiss();
        });
    }
}
