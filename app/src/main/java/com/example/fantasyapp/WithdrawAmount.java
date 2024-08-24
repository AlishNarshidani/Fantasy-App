package com.example.fantasyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class WithdrawAmount extends AppCompatActivity {

    AppCompatButton btn500,btn100,btn50,withdrawButton;
    EditText amount, upiId;
    String amountVal, upiIdStr;

    FirebaseAuth auth;
    FirebaseFirestore db;

    Long fetchedDepositMoney,fetchedWithdrawableMoney,fetchedBonusMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_withdraw_amount);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btn500 = (AppCompatButton) findViewById(R.id.btn500);
        btn100 = (AppCompatButton) findViewById(R.id.btn100);
        btn50 = (AppCompatButton) findViewById(R.id.btn50);
        withdrawButton = (AppCompatButton) findViewById(R.id.withdrawButton);
        amount = (EditText) findViewById(R.id.amount);
        upiId = (EditText) findViewById(R.id.upiId);

        db=FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();



        btn500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount.setText("500");
                amountVal = amount.getText().toString();
            }
        });
        btn100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount.setText("100");
                amountVal = amount.getText().toString();
            }
        });
        btn50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount.setText("50");
                amountVal = amount.getText().toString();
            }
        });

        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                amountVal = amount.getText().toString();
                upiIdStr = upiId.getText().toString();


                if(amountVal.length() == 0 || upiIdStr.length() == 0)
                {
                    if(amountVal.length() == 0) {
                        amount.setError("Enter Amount");
                    }
                    if(upiIdStr.length() == 0)
                    {
                        upiId.setError("Enter UPI ID");
                    }
                }
                else {
                    WithdrawNow(amountVal,new WithdrawCallback(){
                        @Override
                        public void onWithdrawComplete(int result) {
                            if(result==1)
                            {
                                Intent resultIntent = new Intent();
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            }
                        }
                    });

                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public interface WithdrawCallback {
        void onWithdrawComplete(int result);
    }

    public void WithdrawNow(String val, WithdrawCallback callback)
    {
        FirebaseUser user=auth.getCurrentUser();
        String userId=user.getUid();

        // Reference to the document in the 'users' collection
        DocumentReference userDocRef = db.collection("users").document(userId);

        // Fetch the document of current user
        userDocRef.get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if(document!=null && document.exists()) {
                            // The document exists, get all fields as a Map
                            Map<String, Object> fetchUserData = document.getData();

                            if (fetchUserData != null) {
                                // Iterate through the fields and log or use the data
                                for (Map.Entry<String, Object> entry : fetchUserData.entrySet()) {
                                    String key = entry.getKey();
                                    Object value = entry.getValue();

                                    // Log the key and value
                                    Log.d("FirestoreData", key + ": " + value.toString());
                                }

                                // Example: Get specific fields (if needed)
                                fetchedDepositMoney = document.getLong("deposit money");
                                fetchedWithdrawableMoney = document.getLong("withdrawable money");
                                fetchedBonusMoney = document.getLong("bonus money");

                                Log.d("Firestore Wallet Data", fetchedDepositMoney + ", " + fetchedWithdrawableMoney + ", " + fetchedBonusMoney);

                                Long amountVal_long = Long.parseLong(amountVal);

                                if(fetchedWithdrawableMoney < amountVal_long || amountVal_long == 0)
                                {
                                    if(fetchedWithdrawableMoney < amountVal_long) {
                                        amount.setError("Insufficient Balance, your winnings: ₹" + fetchedWithdrawableMoney);
                                    }
                                    else if(amountVal_long == 0) {
                                        amount.setError("Enter some Amount!");
                                    }
                                } else {

                                    callback.onWithdrawComplete(1);

                                    Toast.makeText(getApplicationContext(), "Money Will get credited within 3 days", Toast.LENGTH_SHORT).show();
                                    Long new_withdrawable_balance = fetchedWithdrawableMoney - amountVal_long;

                                    //update wallet data when money deposited
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("withdrawable money", new_withdrawable_balance);
                                    Log.d("withdraw", "money withdrawn, new withdraw balance: " + new_withdrawable_balance);

                                    db.collection("users").document(userId).set(userData, SetOptions.merge())
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(WithdrawAmount.this, "Successfully updated wallet !", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(WithdrawAmount.this, "Error Saving User Data!", Toast.LENGTH_SHORT).show());

                                }

                            }
                        } else {
                            Toast.makeText(WithdrawAmount.this, "No such document!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(WithdrawAmount.this, "Error fetching data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}