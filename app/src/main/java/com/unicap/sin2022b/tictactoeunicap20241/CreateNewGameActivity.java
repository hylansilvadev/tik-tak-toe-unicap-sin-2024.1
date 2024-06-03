package com.unicap.sin2022b.tictactoeunicap20241;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unicap.sin2022b.tictactoeunicap20241.Service.Users;

import java.util.UUID;

public class CreateNewGameActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference("games");

    TextView matchLink;
    Button createRoomButton;
    EditText roomCodeInput;
    String gameId;
    DatabaseReference gameRef;
    Users user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_game);

        matchLink = findViewById(R.id.matchLink);
        createRoomButton = findViewById(R.id.createRoomButton);
        roomCodeInput = findViewById(R.id.roomCodeInput);
        user = Users.getInstance();

        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRoom();
            }
        });
    }

    private void createRoom() {
        gameId = UUID.randomUUID().toString();
        gameRef = mDatabase.child(gameId);

        gameRef.child("playerOne").setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e(TAG, "Error creating room", databaseError.toException());
                } else {
                    gameRef.child("currentPlayer").setValue(user.getName()); // Define o jogador atual
                    matchLink.setText(gameId);
                    listenForGameStart();
                }
            }
        });
    }

    private void listenForGameStart() {
        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String playerOneStatus = dataSnapshot.child("playerOne").child("name").getValue(String.class);
                String playerTwoStatus = dataSnapshot.child("playerTwo").child("name").getValue(String.class);

                if (playerOneStatus != null && playerTwoStatus != null) {
                    // Ambos os jogadores estão conectados, iniciar a GameActivity
                    Intent intent = new Intent(CreateNewGameActivity.this, GameActivity.class);
                    intent.putExtra("gameId", gameId);
                    startActivity(intent);
                    finish(); // Finalizar a CreateNewGameActivity para que não possa voltar
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
