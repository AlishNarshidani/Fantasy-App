package com.example.fantasyapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SelectCapVc extends AppCompatActivity {

    ListView listView;

    CapVcAdapter capVcAdapter;


    private int selectedCaptain = -1;
    private int selectedViceCaptain = -1;

    TextView captainTextView, viceCaptainTextView;
    AppCompatButton saveTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_cap_vc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        captainTextView = findViewById(R.id.captainTextView);
        viceCaptainTextView = findViewById(R.id.viceCaptainTextView);
        saveTeam = findViewById(R.id.saveTeam);
        listView = findViewById(R.id.listView);
        ArrayList<Player> selectedPlayers = (ArrayList<Player>) getIntent().getSerializableExtra("selectedPlayers");


        capVcAdapter = new CapVcAdapter(this,selectedPlayers);
        listView.setAdapter(capVcAdapter);

        saveTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int capPos = capVcAdapter.getSelectedCaptain();
                int vcPos = capVcAdapter.getSelectedViceCaptain();

                Log.d("cap", "onClick: "+capPos);
                Log.d("vc", "onClick: "+vcPos);
                if(capPos != -1 && vcPos != -1)
                {
                    captainTextView.setText((selectedPlayers.get(capPos)).getPlayerName());

                    viceCaptainTextView.setText((selectedPlayers.get(vcPos)).getPlayerName());
                } else {
                    Toast.makeText(SelectCapVc.this, "Please Select Both C & VC", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}