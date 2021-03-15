package com.example.socketclient.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.socketclient.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText nickname = findViewById(R.id.et_loginact_nickname);
        TextView login = findViewById(R.id.tv_loginact_btn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ChatRoomActivity.class);
                intent.putExtra("nickName", nickname.getText().toString());
                startActivity(intent);
                finish();
            }
        }) ;
    }
}