package com.example.fantasyapp;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MinesGridGame extends AppCompatActivity {

    private TextView rulesTextView, triesLeftTextView, title;
    private int triesLeft;
    private int prizePosition;
    private int trapPosition1;
    private int trapPosition2;

    private int betAmount = 0;

    Long fetchedDepositMoney,fetchedWithdrawableMoney,fetchedBonusMoney,totalMoney;
    int winAmount;

    FirebaseAuth auth;
    FirebaseFirestore db;


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

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showGameResult("won");
                    }
                },2000);

            } else if (tappedImage == trapPosition1 || tappedImage == trapPosition2) {

                triesLeft = 0;
                fadeImage(img,R.drawable.minebomb);
                revealPositions();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showGameResult("lost");
                    }
                },2000);

            } else {
                fadeImage(img, R.drawable.tryagain);
                triesLeft--;

                if(triesLeft == 0)
                {
                    revealPositions();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showGameResult("lost");
                        }
                    },2000);
                }
            }


            updateTriesLeft();
        }
        else{
            Toast.makeText(getApplicationContext(), "GAME OVER !", Toast.LENGTH_SHORT).show();
        }
    }



    private void showGameResult(String res)
    {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.mines_game_result,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        ImageView resultImage = dialogView.findViewById(R.id.resultImage);
        TextView win_amount = dialogView.findViewById(R.id.win_amount);

        if (res.equals("won")) {
            winAmount = betAmount*3;
            resultImage.setImageResource(R.drawable.gamewin); // Set image for win
            win_amount.setText("₹"+winAmount);



            handleWin(winAmount, new OnBalanceUpdateCompleteListener() {
                @Override
                public void onSuccess() {
                    Log.d("handleWin", "onSuccess: added the win amount in wallet");
                }

                @Override
                public void onFailure() {

                }
            });

        } else {
            resultImage.setImageResource(R.drawable.gameoverimg); // Set image for loss
            win_amount.setText("Better Luck Next Time !");
        }

        AppCompatButton playAgainBtn = dialogView.findViewById(R.id.playAgainBtn);
        playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onResetGame();
            }
        });

        AppCompatButton exitBtn = dialogView.findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onBackPressed();
            }
        });

        dialog.show();
        int width = getResources().getDimensionPixelSize(R.dimen.dialog_width); // Define this dimension in dimens.xml
        int height = LinearLayout.LayoutParams.WRAP_CONTENT; // Adjust height as needed
        dialog.getWindow().setLayout(width, height);
    }




    public interface OnBalanceUpdateCompleteListener {
        void onSuccess();
        void onFailure();
    }



    private void handleWin(int prizeAmount, OnBalanceUpdateCompleteListener listener)
    {
        FirebaseUser user=auth.getCurrentUser();
        String userId=user.getUid();

        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                fetchedWithdrawableMoney = documentSnapshot.getLong("withdrawable money");
                if (fetchedWithdrawableMoney == null) fetchedWithdrawableMoney = 0L;

                long updatedWithdrawableMoney = fetchedWithdrawableMoney + prizeAmount;

                Map<String, Object> updates = new HashMap<>();
                updates.put("withdrawable money", updatedWithdrawableMoney);

                userDocRef.update(updates)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("handleWin", "successfully updated wallet");
                            addTransactionRecord(userId,winAmount,"Completed","Mines Game Win",listener);
                        })
                        .addOnFailureListener(e -> {});
            }

        }).addOnFailureListener(e -> {});
    }




    private void addTransactionRecord(String userId, int amount, String status, String transactionType, OnBalanceUpdateCompleteListener listener)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("userId", userId);
        transactionData.put("transactionType", transactionType);
        transactionData.put("amount", amount);
        transactionData.put("transactionStatus", status);
        transactionData.put("transactionDate", new com.google.firebase.Timestamp(new java.util.Date()));

        db.collection("transactions")
                .add(transactionData)
                .addOnSuccessListener(documentReference -> {
                    String transactionId = documentReference.getId();
                    Log.d("transactionId", "transactionId: "+transactionId);
                })
                .addOnFailureListener(e -> {
                    Log.d("transaction", "failed");
                });

    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void onResetGame()
    {

        showBetAmountDialog();

    }



    private void showBetAmountDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_bet_amount, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        EditText betAmountEditText = dialogView.findViewById(R.id.betAmountEditText);
        AppCompatButton incrementButton = dialogView.findViewById(R.id.incrementButton);
        AppCompatButton decrementButton = dialogView.findViewById(R.id.decrementButton);
        AppCompatButton confirmButton = dialogView.findViewById(R.id.confirmButton);
        AppCompatButton cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Set the bet amount based on user's input and interaction
        incrementButton.setOnClickListener(v -> {
            String betAmountStr = betAmountEditText.getText().toString();
            betAmount = !betAmountStr.isEmpty() ? Integer.parseInt(betAmountStr) : 0;
            betAmount += 10;
            betAmountEditText.setText(String.valueOf(betAmount));
        });

        decrementButton.setOnClickListener(v -> {
            String betAmountStr = betAmountEditText.getText().toString();
            betAmount = !betAmountStr.isEmpty() ? Integer.parseInt(betAmountStr) : 0;
            if (betAmount > 0) {
                betAmount -= 10;
                betAmountEditText.setText(String.valueOf(betAmount));
            }
        });

        confirmButton.setOnClickListener(v -> {
            String betAmountStr = betAmountEditText.getText().toString();

            if (!betAmountStr.isEmpty() && Integer.parseInt(betAmountStr) > 0) {
                betAmount = Integer.parseInt(betAmountStr);
                dialog.dismiss();
                checkBalanceAndProceed(betAmount);
            } else {
                //betAmountEditText.setError("Please enter a valid amount");
                Toast.makeText(this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            onBackPressed(); // Exit the game if canceled
        });

        dialog.show();
    }

    private void checkBalanceAndProceed(int betAmount)
    {
        db= FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();

        FirebaseUser user=auth.getCurrentUser();

        String userId=user.getUid();

        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                fetchedDepositMoney = documentSnapshot.getLong("deposit money");
                fetchedWithdrawableMoney = documentSnapshot.getLong("withdrawable money");
                fetchedBonusMoney = documentSnapshot.getLong("bonus money");

                if (fetchedBonusMoney == null) fetchedBonusMoney = 0L;
                if (fetchedDepositMoney == null) fetchedDepositMoney = 0L;
                if (fetchedWithdrawableMoney == null) fetchedWithdrawableMoney = 0L;

                totalMoney = fetchedDepositMoney+fetchedWithdrawableMoney+fetchedBonusMoney;

                if (totalMoney >= betAmount) {

                    long remainingAmount = betAmount;

                    // Deduct from bonusMoney first
                    if (fetchedBonusMoney >= remainingAmount) {
                        fetchedBonusMoney -= remainingAmount;
                        remainingAmount = 0;
                    } else {
                        remainingAmount -= fetchedBonusMoney;
                        fetchedBonusMoney = 0L;
                    }

                    // Deduct from depositMoney if needed
                    if (remainingAmount > 0) {
                        if (fetchedDepositMoney >= remainingAmount) {
                            fetchedDepositMoney -= remainingAmount;
                            remainingAmount = 0;
                        } else {
                            remainingAmount -= fetchedDepositMoney;
                            fetchedDepositMoney = 0L;
                        }
                    }

                    // Deduct from withdrawableMoney if needed
                    if (remainingAmount > 0) {
                        if (fetchedWithdrawableMoney >= remainingAmount) {
                            fetchedWithdrawableMoney -= remainingAmount;
                            remainingAmount = 0;
                        } else {
                            fetchedWithdrawableMoney = 0L;
                        }
                    }

                    // can call this method to add transaction and remove the redundant code below to add transaction
                    // do this once check if added code works
//
//                    addTransactionRecord(userId, betAmount,"Mines Game Entry", "Completed", new OnBalanceUpdateCompleteListener() {
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                        @Override
//                        public void onFailure() {
//
//                        }
//                    });

                    Map<String, Object> transactionData = new HashMap<>();
                    transactionData.put("userId", userId);
                    transactionData.put("transactionType", "Mines Game Entry");
                    transactionData.put("amount", betAmount);
                    transactionData.put("transactionStatus", "Completed");
                    transactionData.put("transactionDate", new com.google.firebase.Timestamp(new java.util.Date()));


                    db.collection("transactions")
                            .add(transactionData)
                            .addOnSuccessListener(documentReference -> {
                                String transactionId = documentReference.getId();
                                Log.d("transactionId", "transactionId: "+transactionId);
                            })
                            .addOnFailureListener(e -> {
                                Log.d("transaction", "failed");
                            });


                    Map<String, Object> updates = new HashMap<>();
                    updates.put("bonus money", fetchedBonusMoney);
                    updates.put("deposit money", fetchedDepositMoney);
                    updates.put("withdrawable money", fetchedWithdrawableMoney);

                    // Proceed with deducting the amount and resetting the game
                    userDocRef.update(updates)
                            .addOnSuccessListener(aVoid -> proceedWithGameReset())
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to update balance", Toast.LENGTH_SHORT).show());
                } else {
                    onResetGame();
                    Toast.makeText(this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch balance", Toast.LENGTH_SHORT).show());
    }



    private void proceedWithGameReset() {

        Toast.makeText(this, "You Placed Bet of: "+betAmount, Toast.LENGTH_SHORT).show();

        triesLeft = 4;
        prizePosition = (int) (Math.random() * 16);
        do {
            trapPosition1 = (int) (Math.random() * 16);
        } while (trapPosition1 == prizePosition);

        do {
            trapPosition2 = (int) (Math.random() * 16);
        } while (trapPosition2 == prizePosition || trapPosition2 == trapPosition1);

        Log.d("prizePosition", "prizePosition: " + prizePosition);
        Log.d("bombPosition", "bombPosition: " + trapPosition1 + ", " + trapPosition2);

        for (int i = 0; i < 16; i++) {
            ImageView img = findViewById(getResources().getIdentifier("imageView" + i, "id", getPackageName()));
            img.setImageResource(0);
            img.setEnabled(true);
        }

        updateTriesLeft();
    }





    private void revealPositions()
    {
        for(int i=0;i<16;i++)
        {
            ImageView img = findViewById(getResources().getIdentifier("imageView"+i, "id", getPackageName()));

            if(i == prizePosition)
            {
                fadeImage(img,R.drawable.winnings);
            } else if (i == trapPosition1 || i == trapPosition2) {
                fadeImage(img,R.drawable.minebomb);
            }
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


        rulesTextView = findViewById(R.id.rulesTextView);
        triesLeftTextView = findViewById(R.id.triesLeftTextView);
        title = findViewById(R.id.gameTitle);

        onResetGame();
    }
}