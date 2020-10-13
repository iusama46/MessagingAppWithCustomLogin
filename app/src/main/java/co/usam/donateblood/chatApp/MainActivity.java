package co.usam.donateblood.chatApp;

/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import co.usam.donateblood.R;
import co.usam.donateblood.chatApp.fragment.ChatsFragment;
import co.usam.donateblood.chatApp.fragment.ContactsFragment;
import co.usam.donateblood.constants.Constants;
import co.usam.donateblood.customWidget.CShowProgress;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    public static FirebaseAuth mAuth;
    Handler handler;
    Runnable runnable;
    FrameLayout frameLayout;
    BottomNavigationView chipNavigationBar;
    CShowProgress showProgress = CShowProgress.getInstance();
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Paper.init(this);
        frameLayout = findViewById(R.id.frame_lay);
        chipNavigationBar = findViewById(R.id.nav);


        mAuth = FirebaseAuth.getInstance();
        Paper.init(this);
        try {

            userType = Paper.book().read(Constants.ROLE);
            checkUser(userType);
        } catch (Exception e) {

        }

//        Toast.makeText(this, "" + userType, Toast.LENGTH_SHORT).show();

        chipNavigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.txt1) {
                    setFragment(new ContactsFragment());
                } else if (item.getItemId() == R.id.txt) {
                    setFragment(new ChatsFragment());
                }
                return true;
            }
        });

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(frameLayout.getId(), fragment);
        transaction.commit();
    }

    public void logoutUser() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout?");
        builder.setMessage(" Are you sure you want logout?");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    mAuth.signOut();
                    Paper.book().destroy();
                } catch (Exception e) {

                } finally {
                    Intent intent = new Intent(MainActivity.this, co.usam.donateblood.activities.MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menue, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                logoutUser();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void checkUser(String userType) {
        try {
            final String tempPhone = mAuth.getCurrentUser().getPhoneNumber();//.substring(3);
            showProgress.showProgress(this);
            Log.d("clima" + tempPhone, userType);
            Query phoneQuery = FirebaseDatabase.getInstance().getReference(userType).orderByChild(Constants.PHONE_NUMBER).equalTo(tempPhone);
            phoneQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        boolean isVerified = (boolean) dataSnapshot.child(tempPhone).child("is_verified").getValue();
                        final String name = (String) dataSnapshot.child(tempPhone).child("name").getValue();

                        if (!isVerified) {
                            showProgress.hideProgress();
                            displayMsg(0);
                        } else {
                            try {
                            boolean isApproved = Paper.book().read(Constants.APPROVAL);
                            if (!isApproved) {
                                showProgress.hideProgress();
                                Paper.book().write(Constants.APPROVAL, true);
                                Paper.book().write("name", name);
                                setFragment(new ChatsFragment());
                                displayMsg(1);
                            } else {

                                runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        Paper.book().write(Constants.APPROVAL, true);
                                        Paper.book().write("name", name);
                                        setFragment(new ChatsFragment());
                                        showProgress.hideProgress();
                                    }
                                };

                                handler = new Handler();
                                handler.postDelayed(runnable, 3000);

                            }
                            } catch (Exception e) {
                                showProgress.hideProgress();
                                displayMsg(2);
                            }
                        }
                    } else {
                        showProgress.hideProgress();
                        mAuth.signOut();
                        Paper.book().destroy();
                        displayMsg(2);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    showProgress.hideProgress();
                    Toast.makeText(MainActivity.this, databaseError.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    Paper.book().destroy();
                    displayMsg(2);
                }
            });
        } catch (Exception e) {
            showProgress.hideProgress();
        }
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
                    startActivity(new Intent(MainActivity.this, co.usam.donateblood.activities.MainActivity.class));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null)
            handler.removeCallbacks(runnable);
    }
}