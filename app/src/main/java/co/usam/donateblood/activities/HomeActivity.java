package co.usam.donateblood.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import co.usam.donateblood.R;
import co.usam.donateblood.constants.Constants;
import co.usam.donateblood.customWidget.CShowProgress;
import io.paperdb.Paper;
/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    CShowProgress showProgress = CShowProgress.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Paper.init(this);
        mAuth = FirebaseAuth.getInstance();
        checkUser();
    }

    public void checkUser() {
        final String tempPhone = mAuth.getCurrentUser().getPhoneNumber();//.substring(3);
        showProgress.showProgress(this);
        Query phoneQuery = FirebaseDatabase.getInstance().getReference(Constants.USERS).orderByChild(Constants.PHONE_NUMBER).equalTo(tempPhone);
        phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showProgress.hideProgress();
                if (dataSnapshot.exists()) {
                    boolean isVerified = (boolean) dataSnapshot.child(tempPhone).child("is_verified").getValue();
                    if (!isVerified) {
                        displayMsg(0);
                    } else {
                        boolean isApproved = Paper.book().read(Constants.APPROVAL);
                        if (!isApproved) {
                            Paper.book().write(Constants.APPROVAL, true);
                            displayMsg(1);
                        }
                    }
                } else {
                    mAuth.signOut();
                    Paper.book().destroy();
                    displayMsg(2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showProgress.hideProgress();
                Toast.makeText(HomeActivity.this, databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void displayMsg(int code) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        if (code == 0) {
            builder.setTitle("Approval");
            builder.setMessage("Your account verification is pending for approval from administrator. Try again later or Contact adimn@admin.com");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                }
            });
        } else if (code == 1) {
            builder.setTitle("Congratulations");
            builder.setMessage("Your account is approved by administrator. Thank you for joining our community");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else if (code == 2) {
            builder.setTitle("Error: Session");
            builder.setMessage("Sign in Again");
            builder.setPositiveButton("SignIn", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    startActivity(new Intent(HomeActivity.this, MainActivity.class));
                    finish();
                }
            });

            builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                }
            });
        }
        builder.show();
    }
}