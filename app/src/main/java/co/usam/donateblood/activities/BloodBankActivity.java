package co.usam.donateblood.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import co.usam.donateblood.R;
import co.usam.donateblood.chatApp.MainActivity;
import co.usam.donateblood.constants.Constants;
import co.usam.donateblood.customWidget.CShowProgress;
/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
public class BloodBankActivity extends AppCompatActivity {

    EditText name, location;
    Button saveBtn;
    String phoneNum;
    String uId;
    FirebaseAuth mAuth;
    //CShowProgress showProgress = CShowProgress.getInstance();
    ProgressDialog dialog;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_bank);
        location = findViewById(R.id.location);
        name = findViewById(R.id.name);

        saveBtn = findViewById(R.id.save);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Saving to Db");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference(Constants.BLOOD_BANK);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyData();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        uId = mAuth.getCurrentUser().getUid();
        if (uId == null) {
            uId = "Missing";
        }
        phoneNum = mAuth.getCurrentUser().getPhoneNumber();

        if (phoneNum == null) {
            phoneNum = "+123456789";
        }
    }

    private void verifyData() {
        if (!name.getText().toString().isEmpty()) {
            if (!location.getText().toString().isEmpty()) {
                if (location.getText().toString().length() > 2) {
                    uploadData();
                } else {
                    location.setError("Wrong Address");
                }
            } else {
                location.setError("Required");
            }
        } else {
            name.setError("Required");
        }


    }

    private void uploadData() {

        dialog.show();

        final HashMap<Object, Object> dataMap = new HashMap<Object, Object>();

        dataMap.put("user_id", uId);
        dataMap.put("name", name.getText().toString());
        dataMap.put("is_verified", false);
        dataMap.put(Constants.PHONE_NUMBER, phoneNum);
        dataMap.put("address", location.getText().toString());

        dbRef.child(phoneNum).setValue(dataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dialog.hide();
                Toast.makeText(BloodBankActivity.this, "done", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.hide();
                Toast.makeText(BloodBankActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("clima", e.getMessage());

            }
        });

    }
}