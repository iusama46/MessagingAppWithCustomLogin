package co.usam.donateblood.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class DataVerificationActivity extends AppCompatActivity {

    final List<String> list = new ArrayList<String>();
    EditText cnic, age, name, location;
    Button next;
    ImageView image1, image2;
    Bitmap bitmap1, bitmap2;
    Uri uri1, uri2;
    String url1, url2;
    int requestCodeImages = 0;
    StorageReference mStorageRef;
    DatabaseReference dbRef;
    String phoneNum;
    String bloodGroup;
    String uId;
    boolean isNext = false;
    FirebaseAuth mAuth;
     CShowProgress showProgress = CShowProgress.getInstance();
    ProgressDialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_verification);
        cnic = findViewById(R.id.cnic);
        age = findViewById(R.id.age);
        name = findViewById(R.id.name);

        next = findViewById(R.id.next);
        image1 = findViewById(R.id.pic_one);
        image2 = findViewById(R.id.pic_two);
        location = findViewById(R.id.location);


        mAuth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Saving to Db");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);


        uId = mAuth.getCurrentUser().getUid();
        if (uId == null) {
            uId = "Missing";
        }
        phoneNum = mAuth.getCurrentUser().getPhoneNumber();

        if (phoneNum == null) {
            phoneNum = "+123456789";
        }
        list.clear();
        list.add("Choose Blood Group");

        for (Constants.BloodGroup myVar : Constants.BloodGroup.values()) {
            list.add(myVar.toString());
        }

        final MaterialSpinner spinner = findViewById(R.id.spinner);
        spinner.setItems(list);
        spinner.setBackground(getDrawable(R.drawable.round_editbox));
        spinner.setPadding(25, 35, 25, 35);
        spinner.setDropdownMaxHeight(500);
        spinner.setDropdownHeight(400);
        spinner.setTextColor(getColor(R.color.colorPrimaryDark));
        spinner.setHintColor(getColor(R.color.white));

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                spinner.setTextColor(getColor(R.color.white));
                bloodGroup = item;
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference(Constants.USERS);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        Paper.init(this);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyData();
            }
        });


        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCodeImages = 1;
                ImagePicker.Companion.with(DataVerificationActivity.this)
                        .crop()
                        .compress(512) //// Final image size will be less than 1 MB(Optional)
                        //.saveDir(Utils.getRootDirPath(MainActivity.this))
                        .maxResultSize(1024, 1024)
                        .start();
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCodeImages = 2;
                ImagePicker.Companion.with(DataVerificationActivity.this)
                        .crop()
                        .compress(512) //// Final image size will be less than 1 MB(Optional)
                        //.saveDir(Utils.getRootDirPath(MainActivity.this))
                        .maxResultSize(1024, 1024)
                        .start();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (data != null) {
                Uri imgUri = data.getData();

                try {
                    if (requestCodeImages == 1) {
                        uri1 = imgUri;

                        bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                        image1.setImageBitmap(bitmap1);
                    }
                    if (requestCodeImages == 2) {
                        uri2 = imgUri;
                        bitmap2 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                        image2.setImageBitmap(bitmap2);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, "ImagePicker.getError(data)", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadData() {

        showProgress.showProgress(DataVerificationActivity.this);
//        dialog.show();

        //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        final StorageReference filePath1 = mStorageRef.child(Constants.CNIC_IMAGES + "/" + phoneNum + "/img1");
        final StorageReference filePath2 = mStorageRef.child(Constants.CNIC_IMAGES + "/" + phoneNum + "/img2");
        final StorageReference filePath = filePath1;

        filePath.putFile(uri1).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downUri = task.getResult();
                    Log.d("climatak", "onComplete: Url: " + downUri.toString());
                }
            }
        });


        filePath1.putFile(uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("clima", "onSuccess: uri= " + uri.toString());
                        url1 = uri.toString();
                        filePath2.putFile(uri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filePath2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Log.d("clima", "onSuccess: uri= " + uri.toString());

                                        url2 = uri.toString();

                                        final HashMap<Object, Object> datamap = new HashMap<Object, Object>();

                                        datamap.put("cnic_url1", url1);
                                        datamap.put("cnic_url2", url2);
                                        datamap.put("user_id", uId);
                                        datamap.put("name", name.getText().toString());
                                        datamap.put("cnic_num", cnic.getText().toString());
                                        datamap.put("age", age.getText().toString());
                                        datamap.put("is_verified", false);
                                        datamap.put(Constants.PHONE_NUMBER, phoneNum);
                                        datamap.put("blood_group", bloodGroup);
                                        datamap.put("address", location.getText().toString());

                                        dbRef.child(phoneNum).setValue(datamap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                   showProgress.hideProgress();
                                                dialog.hide();
                                                Toast.makeText(DataVerificationActivity.this, "done", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(DataVerificationActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                Log.d("clima", e.getMessage());

                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showProgress.hideProgress();
                                        dialog.hide();
                                        Log.d("clima1", e.getMessage());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.hide();
                                showProgress.hideProgress();
                                Log.d("clima", e.getMessage());
                            }
                        });
                    }
                });
            }
        });
    }

    private void verifyData() {
        if (!name.getText().toString().isEmpty()) {
            if (!age.getText().toString().isEmpty()) {
                if (!cnic.getText().toString().isEmpty()) {
                    if (cnic.getText().toString().length() > 12) {
                        if (bloodGroup != null && !bloodGroup.equals(list.get(0))) {

                            if (!location.getText().toString().isEmpty()) {
                                if (location.getText().toString().length() > 2) {
                                    if (!isNext) {
                                        LinearLayout linearLayout = findViewById(R.id.lay);
                                        linearLayout.setVisibility(View.GONE);
                                        LinearLayout linearLayout1 = findViewById(R.id.lay_id_card);
                                        linearLayout1.setVisibility(View.VISIBLE);
                                        isNext = true;
                                    } else {
                                        if (bitmap1 != null) {
                                            if (bitmap2 != null) {
                                                uploadData();
                                            } else {
                                                Toast.makeText(DataVerificationActivity.this, "Image missing", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(DataVerificationActivity.this, "Image missing", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    location.setError("Wrong Address");
                                }
                            } else {
                                location.setError("Required");
                            }


                        } else {
                            Toast.makeText(DataVerificationActivity.this, "Choose Blood Group", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        cnic.setError("Wrong CNIC No");
                    }
                } else {
                    cnic.setError("Required");
                }
            } else {
                age.setError("Required");
            }
        } else {
            name.setError("Required");
        }
    }
}