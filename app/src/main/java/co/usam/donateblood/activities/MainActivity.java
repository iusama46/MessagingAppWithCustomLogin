package co.usam.donateblood.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import co.usam.donateblood.R;
import co.usam.donateblood.constants.Constants;
import io.paperdb.Paper;
/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);

        findViewById(R.id.bloodbank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().write(Constants.ROLE, Constants.Role.blood_banks.toString());
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });

        findViewById(R.id.user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().write(Constants.ROLE, Constants.Role.users.toString());
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });
    }
}