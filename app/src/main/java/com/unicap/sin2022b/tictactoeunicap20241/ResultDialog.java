package com.unicap.sin2022b.tictactoeunicap20241;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultDialog extends Dialog {

    private String message;
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

        resultText = findViewById(R.id.messageTV);
        restartButton = findViewById(R.id.startNewBtn);

        resultText.setText(message);

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() instanceof GameActivity) {
                    ((GameActivity) getContext()).restartGame();
                }
                dismiss();
            }
        });
    }
}
