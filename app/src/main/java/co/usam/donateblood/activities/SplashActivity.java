package co.usam.donateblood.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import co.usam.donateblood.R;
import co.usam.donateblood.Utils;
import co.usam.donateblood.constants.Constants;
import io.paperdb.Paper;
/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView loadingTextView = findViewById(R.id.text);
        Paper.init(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (!Utils.isNetworkAvailable(this)) {
            displayMsg("Connectivity issue", "Internet is not Connected");
            loadingTextView.setText("Internet is not Connected, Check yout Internet connection then try again");
            return;
        }

        String uId = Paper.book().read(Constants.uid);
        String phoneNum = Paper.book().read(Constants.phone);
        if (uId != null && phoneNum != null) {
            if (uId.equals(auth.getCurrentUser().getUid())) {
                startActivity(new Intent(getApplicationContext(), co.usam.donateblood.chatApp.MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

    }


    public void displayMsg(String title, String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });

        builder.show();
    }
}