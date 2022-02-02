package gr.indahouse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 101;
    private static final String TAG = "SetupActivity";
    CircleImageView profileImageView;
    Button btnSave, getLocationBtn;
    Uri imageUri;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef;
    StorageReference storageRef;
    ProgressDialog mLoadingBar;
    Toolbar toolbar;
    FusedLocationProviderClient fusedLocationProviderClient;
    private TextInputLayout inputUsername, inputStreet, inputFloor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //init Toolbar
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.setup_profile_title));

        profileImageView = findViewById(R.id.profile_image);
        inputUsername = findViewById(R.id.inputUsername);
        inputStreet = findViewById(R.id.inputStreet);
        inputFloor = findViewById(R.id.inputFloor);
        btnSave = findViewById(R.id.btnSave);
        mLoadingBar = new ProgressDialog(this);
        getLocationBtn = findViewById(R.id.getLocationBtn);


        //init FirebaseDatabase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.ref_users));
        //init FirebaseStorage
        storageRef = FirebaseStorage.getInstance().getReference().child(getString(R.string.ref_profileImage));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationBtn.setOnClickListener(v -> {
            //check permission for location
            if (ActivityCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                //When permission granted
                getCurrentLocation();

            } else {
                ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
        });

        //create onClick listeners
        profileImageView.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //When rotate save instance to reText fields onCreate
        outState.putParcelable("input_setup_image_uri_key", imageUri);
        outState.putString("input_setup_username_key", Objects.requireNonNull(inputUsername.getEditText()).getText().toString());
        outState.putString("input_setup_street_key", Objects.requireNonNull(inputStreet.getEditText()).getText().toString());
        outState.putString("input_setup_floor_key", Objects.requireNonNull(inputFloor.getEditText()).getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Retrieving text if screen rotates and putting it back to where it belongs
        profileImageView.setImageURI(savedInstanceState.getParcelable("input_setup_image_uri_key"));
        Objects.requireNonNull(inputUsername.getEditText()).setText(savedInstanceState.getString("input_setup_username_key"));
        Objects.requireNonNull(inputStreet.getEditText()).setText(savedInstanceState.getString("input_setup_street_key"));
        Objects.requireNonNull(inputFloor.getEditText()).setText(savedInstanceState.getString("input_setup_area_key"));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }

    private void attemptToSaveData() {
        //Function to Save Data to FirebaseDatabase under "Users" with key mAuth <-key
        String username = Objects.requireNonNull(inputUsername.getEditText()).getText().toString();
        String street = Objects.requireNonNull(inputStreet.getEditText()).getText().toString();
        String floor = Objects.requireNonNull(inputFloor.getEditText()).getText().toString();

        //Check for empty inputs
        if (username.isEmpty() || username.length() < 3) {
            showError(inputUsername, getString(R.string.username_is_not_valid));
        } else if (street.isEmpty() || street.length() < 3) {
            showError(inputStreet, getString(R.string.street_is_not_valid));
        } else if (floor.isEmpty()) {
            showError(inputFloor, getString(R.string.floor_is_not_valid));
        } else {
            if (imageUri == null) {
                Toast.makeText(this, getString(R.string.please_select_image_setup), Toast.LENGTH_SHORT).show();
            } else {
                mLoadingBar.setTitle(getString(R.string.adding_setup_profile));
                mLoadingBar.setCanceledOnTouchOutside(false);
                mLoadingBar.show();

                //TODO: add a function to lime image size to 2mb at max
                //Upload the chosen Image
                storageRef.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            //Image successfully uploaded
                            storageRef.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(uri -> {
                                HashMap hashMap = new HashMap();
                                hashMap.put(getString(R.string.ref_users_username), username);
                                hashMap.put(getString(R.string.ref_users_street), street);
                                hashMap.put(getString(R.string.ref_users_floor), floor);
                                hashMap.put(getString(R.string.ref_users_profileImage), uri.toString());

                                //Saving data from EditTextBoxes to FirebaseDatabase
                                mUserRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(o -> {
                                    //If  Data is saved then go to MainActivity
                                    Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    mLoadingBar.dismiss();
                                    Toast.makeText(SetupActivity.this, getString(R.string.setup_complete_msg), Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                                    // Data NOT saved on FirebaseDatabase and Show error
                                    mLoadingBar.dismiss();
                                    Log.d(TAG, "onFailure: failed to save data on FirebaseDatabase");
                                    Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                            });
                        } else {
                            //Image Didn't uploaded
                            Log.d(TAG, "onComplete: Upload Image Task was NOT complete");
                            Toast.makeText(SetupActivity.this, getString(R.string.error_uploading_image), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void showError(TextInputLayout input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            //init location
            Location location = task.getResult();
            if (location != null) {
                Geocoder geocoder = new Geocoder(SetupActivity.this, Locale.getDefault());
                //init address list
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    //set locality to inputStreet
                    Objects.requireNonNull(inputStreet.getEditText()).setText(addresses.get(0).getAddressLine(0));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_image:
                //alert dialog to say for the image size
                AlertDialog.Builder builder = new AlertDialog.Builder(SetupActivity.this);
                builder.setTitle(getString(R.string.alert_dialog_title));
                builder.setPositiveButton(getString(R.string.alert_dialog_yes), (dialog, which) -> {
                    //Opens default image picker from phone/tablet to chose image
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_CODE);
                }).setNegativeButton(getString(R.string.alert_dialog_no), (dialog, which) -> dialog.dismiss());
                builder.show();
                break;
            case R.id.btnSave:
                attemptToSaveData();
                break;
        }
    }
}