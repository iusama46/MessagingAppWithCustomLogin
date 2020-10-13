package co.usam.donateblood.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import co.usam.donateblood.R;
import co.usam.donateblood.chatApp.MainActivity;
import co.usam.donateblood.constants.Constants;
import co.usam.donateblood.customWidget.CShowProgress;
import io.paperdb.Paper;
/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
public class OtpActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button verifyBtn;
    EditText verifyEt;
    String currentPhoneCode;
    String phoneNum;
    ImageView backIcon;
    CShowProgress showProgress = CShowProgress.getInstance();
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            currentPhoneCode = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.VISIBLE);
                verifyEt.setText(code);
                phoneVerification(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            progressBar.setVisibility(View.INVISIBLE);
            Log.d("clima", e.getMessage());
            Toast.makeText(OtpActivity.this, "Error in OTP Verification " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        verifyBtn = findViewById(R.id.verify);
        verifyEt = findViewById(R.id.phonenum);
        progressBar = findViewById(R.id.progressBar);
        backIcon = findViewById(R.id.back_icon);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(true);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        Paper.init(this);

        Intent intent = getIntent();
        phoneNum = intent.getStringExtra("phoneNum");
        if (phoneNum != null) {
            Log.d("clima", phoneNum);
            startPhoneNumberVerification(phoneNum);
        }

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (verifyEt.getText().toString().isEmpty() || verifyEt.getText().toString().length() < 6) {
                    verifyEt.setError("Wrong OTP...");
                    verifyEt.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                phoneVerification(verifyEt.getText().toString());
            }
        });


    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+92" + phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }

    private void phoneVerification(String codeByUser) {
        if (currentPhoneCode != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(currentPhoneCode, codeByUser);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        showProgress.showProgress(OtpActivity.this);
        progressBar.setVisibility(View.INVISIBLE);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        showProgress.hideProgress();
                        if (task.isSuccessful()) {
                            Toast.makeText(OtpActivity.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();

                            Log.d("climaid", user.getUid());
                            Log.d("climaid2", user.getPhoneNumber());


                            String tempNum = user.getPhoneNumber();//.substring(3);
                            Paper.book().write(Constants.uid, user.getUid());
                            Paper.book().write(Constants.phone, tempNum);

                            String role = Paper.book().read(Constants.ROLE);
                            if (role.equals(Constants.Role.users.toString())) {
                                checkUserAlreadyRegisteredOrNot();
                            } else if (role.equals(Constants.Role.blood_banks.toString())) {
                                Toast.makeText(OtpActivity.this, "Bank", Toast.LENGTH_SHORT).show();
                                checkBloodBankAlreadyRegisteredOrNot();
                            }

                        } else {
                            Toast.makeText(OtpActivity.this, "signInWithCredential:failed", Toast.LENGTH_SHORT).show();
                            Log.w("clima", "signInWithCredential:failure", task.getException());
                            progressBar.setVisibility(View.INVISIBLE);
                            //showProgress.showProgress();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void checkUserAlreadyRegisteredOrNot() {
        showProgress.showProgress(OtpActivity.this);
        progressBar.setVisibility(View.INVISIBLE);
        Query phoneQuery = FirebaseDatabase.getInstance().getReference(Constants.USERS).orderByChild(Constants.PHONE_NUMBER).equalTo("+92" + phoneNum);
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                progressBar.setVisibility(View.VISIBLE);
//                showProgress.hideProgress();

                String role = Paper.book().read(Constants.ROLE);
                showProgress.hideProgress();
                if (dataSnapshot.exists()) {

                    boolean isVerified = (boolean) dataSnapshot.child("+92" + phoneNum).child("is_verified").getValue();
                    Paper.book().write(Constants.APPROVAL, isVerified);
                    if (role.equals(Constants.Role.users.toString())) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                } else {
                    if (role.equals(Constants.Role.users.toString())) {
                        Paper.book().write(Constants.APPROVAL, false);
                        Intent intent = new Intent(OtpActivity.this, DataVerificationActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress.hideProgress();
                //progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(OtpActivity.this, databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkBloodBankAlreadyRegisteredOrNot() {
        showProgress.showProgress(OtpActivity.this);
        progressBar.setVisibility(View.INVISIBLE);
        Query phoneQuery = FirebaseDatabase.getInstance().getReference(Constants.BLOOD_BANK).orderByChild(Constants.PHONE_NUMBER).equalTo("+92" + phoneNum);
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    showProgress.hideProgress();
                String role = Paper.book().read(Constants.ROLE);
                if (dataSnapshot.exists()) {
                    boolean isVerified = (boolean) dataSnapshot.child("+92" + phoneNum).child("is_verified").getValue();
                    Paper.book().write(Constants.APPROVAL, isVerified);
                    if (role.equals(Constants.Role.blood_banks.toString())) {
                        Toast.makeText(OtpActivity.this, "exists", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                } else {
                    Paper.book().write(Constants.APPROVAL, false);
                    Intent intent = new Intent(OtpActivity.this, BloodBankActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress.hideProgress();
                //progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(OtpActivity.this, databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    
}