package com.example.fantasyapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfiePage extends AppCompatActivity {

    Button userButton,walletButton,rulesButton,contectButton,termButton,logoutButton;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profie_page);
            userButton = findViewById(R.id.userButton);
            walletButton = findViewById(R.id.walletButton);
            rulesButton = findViewById(R.id.rulesButton);
            contectButton = findViewById(R.id.contectButton);
            termButton = findViewById(R.id.termsButton);
            logoutButton = findViewById(R.id.logutButton);

        rulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start SecondActivity
                Intent intent = new Intent(ProfiePage.this, FantsyPoint.class);
                startActivity(intent);
            }
        });

    }
}