package com.example.fantasyapp;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Otp extends AppCompatActivity {

    TextView instruction;
    Button verify;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        instruction=findViewById(R.id.textView3);
        verify=findViewById(R.id.button4);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user=auth.getCurrentUser();
                if(user!=null)
                {
                    user.reload().addOnCompleteListener(task ->{
                        if(user.isEmailVerified())
                        {
                            Intent i=getIntent();
                            String name=i.getStringExtra("name");
                            String email=i.getStringExtra("email");
                            String mno=i.getStringExtra("mno");
                            String dob=i.getStringExtra("dob");

                            String userId=user.getUid();

                            Map<String,Object> userData = new HashMap<>();
                            userData.put("name",name);
                            userData.put("email",email);
                            userData.put("mno",mno);
                            userData.put("dob",dob);

                            db.collection("users").document(userId).set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(Otp.this,"Successfully Signed Up!",Toast.LENGTH_SHORT).show();
                                        Intent j=new Intent(Otp.this,MainActivity.class);
                                        startActivity(j);
                                    }).addOnFailureListener(e ->Toast.makeText(Otp.this,"Error Saving User Data!",Toast.LENGTH_SHORT).show());
                        }
                    });
                }
            }
        });
    }
}