package com.unicap.sin2022b.tictactoeunicap20241;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.unicap.sin2022b.tictactoeunicap20241.Service.Users;

public class MainMenuActivity extends AppCompatActivity {

    private ImageView userImage;
    private TextView userName;
    private Button findGame, createGame, showHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);

        userImage = findViewById(R.id.userAvatar);
        userName = findViewById(R.id.userName);
        findGame = findViewById(R.id.FindGame);
        createGame = findViewById(R.id.NewGame);
        Users user = Users.getInstance();

        Glide.with(this).load(user.getProfile()).transform(new CircleCrop()).into(userImage);

        userName.setText(user.getName());

        findGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, FindNewGameActivity.class);
                startActivity(intent);
            }
        });

        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, CreateNewGameActivity.class);
                startActivity(intent);
            }
        });
    }
}
