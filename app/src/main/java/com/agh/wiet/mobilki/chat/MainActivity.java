package com.agh.wiet.mobilki.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static String IP = "ip";
    public static String NICK = "nick";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        final EditText ipAddressEditText = findViewById(R.id.ipAddressEditText);
        final EditText nicknameEditText = findViewById(R.id.nicknameEditText);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SimpleChatActivity.class);
                intent.putExtra(IP, ipAddressEditText.getText().toString());
                intent.putExtra(NICK, nicknameEditText.getText().toString());
                startActivity(intent);
            }
        });
    }
}
