package com.example.fantasyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText email,pass;
    Button login;
    TextView forgot,signup;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email=findViewById(R.id.editTextText);
        pass=findViewById(R.id.editTextText2);
        login=findViewById(R.id.button);
        forgot=findViewById(R.id.textView);
        signup=findViewById(R.id.textView2);
        auth=FirebaseAuth.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if(isLoggedIn)
        {
            Intent i = new Intent(getApplicationContext(), HomePage.class);
            startActivity(i);
            finish();
        }
        else {
            pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signup.setPaintFlags(signup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                    Intent i = new Intent(MainActivity.this, SignUp.class);
                    startActivity(i);
                }
            });

            forgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    forgot.setPaintFlags(forgot.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                    Intent i = new Intent(MainActivity.this, ForgotPassword.class);
                    startActivity(i);
                }
            });


            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String em = email.getText().toString().trim();
                    String password = pass.getText().toString().trim();

                    if (em.isEmpty() || password.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Empty field", Toast.LENGTH_SHORT).show();
                    }

                    else {
                        auth.signInWithEmailAndPassword(em, password).addOnCompleteListener(MainActivity.this, task -> {
                            if (task.isSuccessful()) {
                                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.putString("email", email.getText().toString());
                                editor.apply();
                                Log.d("email", email.getText().toString());

                                Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), HomePage.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(MainActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }
    }
}