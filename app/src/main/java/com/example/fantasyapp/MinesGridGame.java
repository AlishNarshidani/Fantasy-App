package com.example.fantasyapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MinesGridGame extends AppCompatActivity {

    private TextView rulesTextView, triesLeftTextView, title;
    private int triesLeft;
    private int prizePosition;


    public void onTileClick(View view)
    {

        if(triesLeft > 0)
        {
            ImageView img = (ImageView) view;

            if (!img.isEnabled()) {
                return; // Do nothing if the tile has already been clicked
            }

            int tappedImage = Integer.parseInt(view.getTag().toString());
            img.setEnabled(false);

            if(tappedImage == prizePosition)
            {
                triesLeft = 0;
                fadeImage(img, R.drawable.winnings);
            }
            else {
                fadeImage(img, R.drawable.tryagain);
            }

            triesLeft--;
            updateTriesLeft();
        }
        else{
            Toast.makeText(getApplicationContext(), "GAME OVER !", Toast.LENGTH_SHORT).show();
        }
    }

    private void fadeImage(ImageView imageView, int newImageResource) {
        imageView.setImageResource(newImageResource);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000); // Duration of the fade animation in milliseconds
        imageView.startAnimation(fadeIn);
    }

    private void updateTriesLeft() {
        triesLeftTextView.setText("Tries Left: " + triesLeft);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mines_grid_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        triesLeft = 4;
        prizePosition = (int) (Math.random() * 16);
        Log.d("prizePosition", "prizePosition: "+prizePosition);


        rulesTextView = findViewById(R.id.rulesTextView);
        triesLeftTextView = findViewById(R.id.triesLeftTextView);
        title = findViewById(R.id.gameTitle);

        updateTriesLeft();

    }
}