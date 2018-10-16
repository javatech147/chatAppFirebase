package com.waytojava.ichatapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.waytojava.ichatapp.R;
import com.waytojava.ichatapp.firebase.FirebaseUtils;
import com.waytojava.ichatapp.firebase.MyFirebaseUser;
import com.waytojava.ichatapp.utils.Utils;
import com.waytojava.ichatapp.utils.Validations;

import java.io.ByteArrayOutputStream;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SignupActivity.class.getSimpleName();
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 23;
    private static final int REQUEST_PERMISSION_SETTING = 24;
    private static final int OPEN_GALLERY_CONSTANT = 34;
    private static final int OPEN_CAMERA_CONSTANT = 44;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private TextView tvAlreadyAMember;
    private CardView cvRegister;
    private Context context;
    private CircleImageView circleImageView;

    private FirebaseAuth mAuth;
    private DatabaseReference mRootReference;
    private String deviceToken;

    private ImageView ivUploadProfileImage;
    private SharedPreferences permissionStatus;
    private String imagePath;
    private String tempFileLocation;
    private byte[] profileImageCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getId();
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        context = SignupActivity.this;
        mRootReference = FirebaseDatabase.getInstance().getReference();

        Task<InstanceIdResult> task = FirebaseInstanceId.getInstance().getInstanceId();
        task.addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                deviceToken = instanceIdResult.getToken();
            }
        });

    }

    private void getId() {
        etName = findViewById(R.id.et_name_signup);
        etEmail = findViewById(R.id.et_email_signup);
        etPassword = findViewById(R.id.et_password_signup);
        tvAlreadyAMember = findViewById(R.id.tv_already_a_member);
        cvRegister = findViewById(R.id.cv_register);
        cvRegister.setOnClickListener(this);
        tvAlreadyAMember.setText(Html.fromHtml(getString(R.string.txt_already_a_member_text)));
        tvAlreadyAMember.setOnClickListener(this);
        ivUploadProfileImage = findViewById(R.id.iv_upload_profile_image);
        ivUploadProfileImage.setOnClickListener(this);
        circleImageView = findViewById(R.id.circle_image_view_profile);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_already_a_member:
                onBackPressed();
                break;

            case R.id.cv_register:
                Utils.hideKeyboard(this);
                if (Validations.validateInputFieldsSignup(this,
                        etName.getText().toString().trim(),
                        etEmail.getText().toString().trim(),
                        etPassword.getText().toString().trim())) {
                    registerUserToFirebaseAuth();
                }
                break;

            case R.id.iv_upload_profile_image:
                checkForStoragePermission();
                break;
        }
    }

    private void checkForStoragePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(SignupActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Utils.snackbar(context, "Go to Permissions to Grant Storage");
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
            }


            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
            editor.commit();


        } else {
            //You already have the permission, just go ahead.
            selectGalleryOrCamera();
        }
    }//end of check for Storage permission.

    private void registerUserToFirebaseAuth() {
        Utils.showProgressDialog(this);
        Task<AuthResult> task = mAuth.createUserWithEmailAndPassword(etEmail.getText().toString().trim(), etPassword.getText().toString().trim());
        task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Utils.dismissProgressDialog();
                if (task.isSuccessful()) {
                    String firebaseId = task.getResult().getUser().getUid();

                    // Storage Image to Firebase Storage.
                    addProfileImageToFirebaseStorage(firebaseId);

                } else {
                    Utils.toast(context, "Some Error Occurs");
                }
            }
        });
    }

    private void addProfileImageToFirebaseStorage(final String firebaseId) {

        Utils.showProgressDialog(this);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference
                .child(FirebaseUtils.PROFILE_IMAGE)
                .child(firebaseId)
                .putBytes(profileImageCamera)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Utils.dismissProgressDialog();


                        // Get Download Url of Profile Image
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Utils.log(TAG, "Download Url : " + uri.toString());
                                addUserToFirebaseDatabase(firebaseId, uri.toString());
                            }
                        });

//                        String bucket = taskSnapshot.getMetadata().getBucket();
//                        String name = taskSnapshot.getMetadata().getName();
//                        String path = taskSnapshot.getMetadata().getPath();
//                        Utils.log(TAG, "Bucket : " + bucket);   // ichatapp-f1b57.appspot.com
//                        Utils.log(TAG, "Name : " + name);       // eMpfs5xMNaN97qdufZ7mFX6Qc7f2
//                        Utils.log(TAG, "Path : " + path);       // profile_image/eMpfs5xMNaN97qdufZ7mFX6Qc7f2
                    }
                });
    }

    private void selectGalleryOrCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.txt_gallery, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, OPEN_GALLERY_CONSTANT);
            }
        });

        builder.setNegativeButton(R.string.txt_camera, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openCamera();
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, OPEN_CAMERA_CONSTANT);
            }
        });

        builder.setNeutralButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void openCamera() {
        final String captureDirectory = Environment.getExternalStorageDirectory() + "/" + Utils.FOLDER_NAME + "/";

        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(captureDirectory);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        }).start();

        imagePath = captureDirectory + Utils.setImageName();
        Utils.log(TAG, "Image Path : " + imagePath);
        Utils.log(TAG, "Package name : " + getPackageName());
        Uri imageFileUri = FileProvider.getUriForFile(context, "" + getPackageName(), new File(imagePath));
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(cameraIntent, OPEN_CAMERA_CONSTANT);
    }

    private void addUserToFirebaseDatabase(String firebaseUserId, String imagePath) {
        Utils.showProgressDialog(this);
        MyFirebaseUser myFirebaseUser = new MyFirebaseUser(etName.getText().toString().trim(),
                etEmail.getText().toString().trim(), deviceToken, imagePath);
        mRootReference.child(FirebaseUtils.USERS)
                .child(firebaseUserId)
                .child(FirebaseUtils.CREDENTIALS)
                .setValue(myFirebaseUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Utils.dismissProgressDialog();
                        if (task.isSuccessful()) {
                            Utils.toast(context, getString(R.string.txt_register_successfully));
                            Intent homeIntent = new Intent(context, MainActivity.class);
                            startActivity(homeIntent);
                            finishAffinity();
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_CAMERA_CONSTANT) {
            if (resultCode == RESULT_OK) {
                try {
                    //Compress Image
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    //tempFileLocation = Utils.saveImageToGivenFolder(bitmap, Utils.FOLDER_NAME);
                    //Utils.log(TAG, "Temp File Location  -- " + tempFileLocation);
                    //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    circleImageView.setImageBitmap(bitmap);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                    profileImageCamera = baos.toByteArray();
                } catch (OutOfMemoryError | Exception error) {
                    error.printStackTrace();
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        }

        if (requestCode == OPEN_GALLERY_CONSTANT) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                Bitmap bitmap = null;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    if (bitmap != null) {
                        // Here you can use bitmap in your application ...
                        circleImageView.setImageBitmap(bitmap);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                        profileImageCamera = baos.toByteArray();
                    }
                } catch (Exception e) {
                    // Manage exception ...
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}