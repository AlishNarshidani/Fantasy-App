package com.example.fantasyapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class WallletFragment extends Fragment {

    AppCompatButton addcash;
    TextView total_balance_amount, unutilized_balance, winnings_balance, cash_bonus_amount;
    Long fetchedDepositMoney,fetchedWithdrawableMoney,fetchedBonusMoney;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_walllet, container, false);

        addcash = view.findViewById(R.id.add_cash_button);
        total_balance_amount = view.findViewById(R.id.total_balance_amount);
        unutilized_balance = view.findViewById(R.id.unutilized_balance);
        winnings_balance = view.findViewById(R.id.winnings_balance);
        cash_bonus_amount = view.findViewById(R.id.cash_bonus_amount);

        db=FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();




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

                                Long totalMoney = fetchedDepositMoney+fetchedWithdrawableMoney+fetchedBonusMoney;


                                Log.d("Firestore Wallet Data", fetchedDepositMoney + ", " + fetchedWithdrawableMoney + ", " + fetchedBonusMoney + " total:" +totalMoney);


                                unutilized_balance.setText("₹" +fetchedDepositMoney.toString());
                                winnings_balance.setText("₹" +fetchedWithdrawableMoney.toString());
                                cash_bonus_amount.setText("₹" +fetchedBonusMoney.toString());
                                total_balance_amount.setText("₹" + totalMoney);

                            }
                        } else {
                            Log.d("error", "No such document!");
                            //Toast.makeText(getActivity(), "No such document!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("error", "Error fetching data!");
                        //Toast.makeText(getActivity(), "Error fetching data!", Toast.LENGTH_SHORT).show();
                    }
                });


        addcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PaymentAmount.class);
                startActivity(intent);
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inflate the layout for this fragment
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        updateBalance();
    }

    private void updateBalance()
    {
        db=FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();

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

                                Long totalMoney = fetchedDepositMoney+fetchedWithdrawableMoney+fetchedBonusMoney;


                                Log.d("Firestore Wallet Data", fetchedDepositMoney + ", " + fetchedWithdrawableMoney + ", " + fetchedBonusMoney + " total:" +totalMoney);


                                unutilized_balance.setText("₹" +fetchedDepositMoney.toString());
                                winnings_balance.setText("₹" +fetchedWithdrawableMoney.toString());
                                cash_bonus_amount.setText("₹" +fetchedBonusMoney.toString());
                                total_balance_amount.setText("₹" + totalMoney);

                            }
                        } else {
                            Log.d("error", "No such document!");
                            //Toast.makeText(getActivity(), "No such document!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("error", "Error fetching data!");
                        //Toast.makeText(getActivity(), "Error fetching data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}