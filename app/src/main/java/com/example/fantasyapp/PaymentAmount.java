package com.example.fantasyapp;

import android.app.Activity;
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

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class PaymentAmount extends AppCompatActivity implements PaymentResultListener {

    AppCompatButton btn500,btn100,btn50,payButton;
    EditText amount;
    String amountVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_amount);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn500 = (AppCompatButton) findViewById(R.id.btn500);
        btn100 = (AppCompatButton) findViewById(R.id.btn100);
        btn50 = (AppCompatButton) findViewById(R.id.btn50);
        payButton = (AppCompatButton) findViewById(R.id.payButton);
        amount = (EditText) findViewById(R.id.amount);



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

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentNow(amountVal);
            }
        });
    }

    public void PaymentNow(String val)
    {
        final Activity activity = this;

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_lxZ3tAXnEknadJ");
        checkout.setImage(R.mipmap.ic_launcher);

        double finalAmount = Double.parseDouble(val)*100;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "CricMania");
            options.put("description", "Reference No. #123456");
            options.put("image", "http://example.com/image/rzp.jpg");
            //options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", finalAmount+"");//pass amount in currency subunits
            options.put("prefill.email", "gaurav.kumar@example.com");
            options.put("prefill.contact","9988776655");
//            JSONObject retryObj = new JSONObject();
//            retryObj.put("enabled", true);
//            retryObj.put("max_count", 4);
//            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e("payError", "Error in starting Razorpay Checkout", e);
        }

    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getApplicationContext(), "Payment Failed!", Toast.LENGTH_SHORT).show();
    }
}