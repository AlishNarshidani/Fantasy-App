package com.example.fantasyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUp extends AppCompatActivity {

    EditText name,email,mno,pass;
    Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        name=findViewById(R.id.editTextText3);
        email=findViewById(R.id.editTextText4);
        mno=findViewById(R.id.editTextText5);
        pass=findViewById(R.id.editTextText6);
        signup=findViewById(R.id.button3);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid=true;
                if(name.getText().toString().trim().isEmpty())
                {
                    name.setError("Empty!");
                    isValid=false;
                }

                if(!emailValid(email.getText().toString()))
                {
                    email.setError("Email is incorrect!");
                    isValid=false;
                }

                if(!mobileValid(mno.getText().toString()))
                {
                    mno.setError("10 digit Mobile Number!");
                    isValid=false;
                }

                if(!passwordValid(pass.getText().toString()))
                {
                    pass.setError("Length should be atleast 7!");
                    isValid=false;
                }

                if(isValid==true)
                {
                    Toast.makeText(SignUp.this, "Successfully Signed Up", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(SignUp.this,MainActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(SignUp.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean emailValid(String email)
    {
        return email!= null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean mobileValid(String mobile)
    {
        String pattern="^\\d{10}$";
        return mobile!=null && mobile.matches(pattern);
    }

    private boolean passwordValid(String pass)
    {
        String pattern="^.{7,}$";
        return pass!=null && pass.matches(pattern);
    }
}