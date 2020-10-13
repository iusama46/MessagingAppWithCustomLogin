package co.usam.donateblood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import co.usam.donateblood.R;
import co.usam.donateblood.constants.Constants;

/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
public class LoginActivity extends AppCompatActivity {
    Button verifyBtn;
    EditText phoneNumEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        verifyBtn = findViewById(R.id.verify);
        phoneNumEt = findViewById(R.id.phonenum);
        phoneNumEt.requestFocus();
        getSupportActionBar().hide();

        Intent intent = getIntent();
        String role = intent.getStringExtra("role");
        if (role != null) {
            Log.d("clima", role);
        } else {
            role = Constants.Role.users.toString();
        }
        //#     mAuth = FirebaseAuth.getInstance();
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!phoneNumEt.getText().toString().isEmpty()) {
                    if (phoneNumEt.getText().toString().length() == 11) {
                        String phoneNum = phoneNumEt.getText().toString().substring(1);
                        startActivity(new Intent(LoginActivity.this, OtpActivity.class).putExtra("phoneNum", phoneNum));
                    } else {
                        phoneNumEt.setError("Enter correct phone no");
                    }
                } else {
                    phoneNumEt.setError("PhoneNum required");
                }
            }
        });


    }
}