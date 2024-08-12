package com.example.fantasyapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfiePage extends AppCompatActivity {

    private TextView emailText;

    private TextView textViewName;
    private TextView textViewEmail;

    private TextView textViewDOB;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Assuming EdgeToEdge is a valid method in your project
        setContentView(R.layout.activity_profie_page);

        textViewName = findViewById(R.id.displayName);
        textViewEmail = findViewById(R.id.displayEmail);

        textViewDOB = findViewById(R.id.displayDOB);

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "user");

        emailText = findViewById(R.id.textView4);
        emailText.setText(email);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            fetchUserData(userId);
        } else {
            Log.e("ProfiePage", "No user is logged in");
        }
    }

    private void fetchUserData(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String userName = document.getString("name");
                                String userEmail = document.getString("email");
                                String userDOB = document.getString("dob");

                                // Display user details
                                textViewName.setText(userName);
                                textViewEmail.setText(userEmail);
                                textViewDOB.setText(userDOB);
                            } else {
                                Log.d("ProfiePage", "No such document");
                            }
                        }
                    }
                });
    }
}