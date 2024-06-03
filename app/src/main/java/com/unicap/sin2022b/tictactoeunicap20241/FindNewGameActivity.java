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

public class FindNewGameActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference("games");

    EditText roomCodeInput;
    Button joinRoomButton;
    TextView statusText;
    Users user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_new_game);

        roomCodeInput = findViewById(R.id.roomCodeInput);
        joinRoomButton = findViewById(R.id.joinRoomButton);
        statusText = findViewById(R.id.statusText);
        user = Users.getInstance();

        joinRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinRoom();
            }
        });
    }

    private void joinRoom() {
        String gameId = roomCodeInput.getText().toString().trim();
        DatabaseReference gameRef = mDatabase.child(gameId);

        gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String playerOneStatus = dataSnapshot.child("playerOne").child("name").getValue(String.class);
                    String playerTwoStatus = dataSnapshot.child("playerTwo").child("name").getValue(String.class);

                    if (playerOneStatus == null || playerOneStatus.equals("waiting")) {
                        gameRef.child("playerOne").setValue(user, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.e(TAG, "Error joining room as player one", databaseError.toException());
                                } else {
                                    listenForGameStart(gameRef, gameId);
                                }
                            }
                        });
                    } else if (playerTwoStatus == null || playerTwoStatus.equals("waiting")) {
                        gameRef.child("playerTwo").setValue(user, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.e(TAG, "Error joining room as player two", databaseError.toException());
                                } else {
                                    listenForGameStart(gameRef, gameId);
                                }
                            }
                        });
                    } else {
                        statusText.setText("Room is full");
                    }
                } else {
                    statusText.setText("Room code invalid");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void listenForGameStart(DatabaseReference gameRef, String gameId) {
        gameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String playerOneStatus = dataSnapshot.child("playerOne").child("name").getValue(String.class);
                String playerTwoStatus = dataSnapshot.child("playerTwo").child("name").getValue(String.class);

                if (playerOneStatus != null && playerTwoStatus != null) {
                    // Ambos os jogadores estão conectados, iniciar a GameActivity
                    Intent intent = new Intent(FindNewGameActivity.this, GameActivity.class);
                    intent.putExtra("gameId", gameId);
                    startActivity(intent);
                    finish(); // Finalizar a FindNewGameActivity para que não possa voltar
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
