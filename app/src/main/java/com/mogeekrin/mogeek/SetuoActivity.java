package com.mogeekrin.mogeek;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class SetuoActivity extends AppCompatActivity {
    private androidx.appcompat.widget.Toolbar setupToolbar;
    private Uri mainImageUri = null;
    private CircleImageView setupImage;
    private EditText setup_name;
    private Button setup_btn;
private boolean isChanged = false;
    //SETTING USER_ID
    private String user_id;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    //private boolean isChanged = false;
//FOR FIREBASE FIRESTORE VARIABLE
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBar;
//private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setuo);
        setupToolbar = findViewById(R.id.Toolbar_setup);
        setSupportActionBar(setupToolbar);

        //INITIALIZING FIREBASEAUTH AND STORAGE REFERENCE
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
//progressDialog = new ProgressDialog(this);
        setupImage = findViewById(R.id.setupimage);
        setup_name = findViewById(R.id.setup_name);
        setup_btn = findViewById(R.id.setup_btn);

//GETTING USER ID FROM FIREBASE
        user_id = firebaseAuth.getUid();

//INITIALIZING firebasefirestore
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.setup_progressBar);

        progressBar.setVisibility(View.VISIBLE);
        setup_btn.setEnabled(false);


        //RETERIVING DATA FROM FORESTORE
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                //IF TASK IS SUCCESSFULL
                if (task.isSuccessful()) {
//IF THE DATA EXISTS
                    if (task.getResult().exists()) {

//                        Toast.makeText(SetuoActivity.this, "Data exists", Toast.LENGTH_LONG).show();

                        //GETTING NAME AND STRING FROM FIREBASE
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        //IF IMAGEEXISTS SAVE IF OFFLINE
                        mainImageUri = Uri.parse(image);

                        //SETTING THE NAME IN SETUPNAME FIELD
                        setup_name.setText(name);
                        //FOR IMAGE USING LIBRARY NAEM GLIDE
                        //SETING UP PLACEHOLDER FOR THE IMAGEVIEW WHILE THE IMAGE IS LAODING
                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);
                        Glide.with(SetuoActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);

                    }


//IF DATA DOSENT EXISTS
                    else {


//                        String error = task.getException().getMessage();

                        Toast.makeText(SetuoActivity.this, "Data dosent exists in storage", Toast.LENGTH_LONG).show();


                    }


                }
//                else {
//
//
//
//                }

                progressBar.setVisibility(View.INVISIBLE);
                setup_btn.setEnabled(true);
            }

        });

        setup_btn.setOnClickListener(new View.OnClickListener()

        {

            @Override
            public void onClick (View v) {
                final String user_name = setup_name.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
//IF THE IMAGE IS CHANGED
                if (isChanged) {
                    //CHECK IF USER_NAME IS NOT EMPTY AND PROFILE PICTURE IS NOT EMPTY
                    if (!TextUtils.isEmpty(user_name) && mainImageUri != null) {

//            if (!TextUtils.isEmpty(user_name)){
                        final String user_id = firebaseAuth.getCurrentUser().getUid();
//            progressDialog.setMessage("Uploading....");
//            progressDialog.show();

                        //SAVING PROFILE_PICTURE IN FIREBASE STORAGE CHILD IS FOLDER
                        final StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");

                        Task<Uri> uriTask = image_path.putFile(mainImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (task.isSuccessful()) {
                                    return image_path.getDownloadUrl();
                                }
                                return image_path.getDownloadUrl();

                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {


                                //CHECK IF TASK IS SUSSEFULL OR NOT
                                if (task.isSuccessful()) {

                                    storeFirestore(task, user_name);

                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);

                                    String error = task.getException().getMessage();
                                    Toast.makeText(SetuoActivity.this, "Error : " + error, Toast.LENGTH_LONG).show();

                                }


                            }


                        });


                        //Firebase getdownloadurl() error


                        //TO STORE IMAGE URI INTO THAT PATH
//            image_path.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//
//                    //CHECK IF TASK IS SUSSEFULL OR NOT
//                    if (task.isSuccessful()){
//
//
//    //GET IMAGE URI
//                        Uri download_uri = task.getResult();
//
//                        Map<String,String> userMap = new HashMap<>();
//
//                        userMap.put("name",user_name);
//                        userMap.put("image",download_uri.toString());
//
//firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//    @Override
//    public void onComplete(@NonNull Task<Void> task) {
//
//        if (task.isSuccessful()){
//            Toast.makeText(SetuoActivity.this,"The user settings are updated",Toast.LENGTH_LONG).show();
//            Intent mainIntent = new Intent(SetuoActivity.this,MainActivity.class);
//            startActivity(mainIntent);
//            finish();
//
//        }else{
//
//            String error = task.getException().getMessage().toString();
//            Toast.makeText(SetuoActivity.this,"Error :" + error,Toast.LENGTH_LONG).show();
//
//        }
//
//
//    }
//});
////                        progressDialog.dismiss();
//                        progressBar.setVisibility(View.INVISIBLE);
//                        Toast.makeText(SetuoActivity.this,"Successful",Toast.LENGTH_LONG).show();
//
//                    }else{progressBar.setVisibility(View.INVISIBLE);
//
//                        String error = task.getException().getMessage();
//                        Toast.makeText(SetuoActivity.this,"Error : " + error,Toast.LENGTH_LONG).show();
//
//                    }
//
//
//
//
//                }
//            });


                    }



                }
                //IF THE IMAGE IS NOT CHANGED
                else {
                    storeFirestore(null, user_name);
                }

                }

        });
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//TODO :
                //1.SETUP PERMISSION FOR WRITING AND READING EXTERNAL STORAGE
                //2.TO SETUP PROFILE IMAGE
                //3.REMEMBER CIRCLE IMAGE VIEW

                //TO CHECK THE BUILD VERSION OF THE OS
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    //IF THE PERMISSION IS GRANTED OR NOT
                    if (ContextCompat.checkSelfPermission(SetuoActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        //ASKING FOR PERMISSION (SURROUND WITH ARRAY MANIFEST)
                        ActivityCompat.requestPermissions(SetuoActivity.this, new String[]{READ_EXTERNAL_STORAGE}, 1);
//          Toast.makeText(SetuoActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    } else {
//
                        //AFTER GETTING THE PERMISSION TAKE THE IMAGE AND CROP IT
                        BringImagePicker();
                    }

                }
//IF SDK VERSION IS BELOW MARSHMELLOW6.0
                else {
                    BringImagePicker();

                }


            }
        });





    }

    private void storeFirestore(@NonNull Task<Uri> task,String user_name) {
        Uri download_uri;
        //IF IMAGE IS CHANGED

        //GET IMAGE URI
        if (task != null) {
            download_uri = task.getResult();
        }else {


            //IF IMAGE IS NOT CHANGED


            download_uri = mainImageUri;
        }
        //STORING DATA INTO FIREBASE FIRESTORE

        //STORING DATA INTO MAP FORMAT
        //AS USERNAME AND IMAGEDOWNLOADURI BOTH AS STRING DATATYPE
        Map<String, String> userMap = new HashMap<>();

        userMap.put("name", user_name);
        userMap.put("image", download_uri.toString());


//COLLECTING DATA AND SETTING ONCOMPLETELISTENER
        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(SetuoActivity.this, "The user settings are updated.", Toast.LENGTH_LONG).show();
                    //SENDING THE USER FROM SETTINGACTIVATY TO MAINACTIVITY
                    Intent mainIntent = new Intent(SetuoActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else {
//GETTING ERROR FROM FIREBASE AND STORING IT IN ERROR VARIABLE
                    String error = task.getException().getMessage().toString();
                    //SHOWING THE ERROR AS A TOAST TO THE USER
                    Toast.makeText(SetuoActivity.this, "Firestore Error :" + error, Toast.LENGTH_LONG).show();

                }


            }
        });
//                        progressDialog.dismiss();
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(SetuoActivity.this, "Successful", Toast.LENGTH_LONG).show();



    }


    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetuoActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                //IF THE IMAGE IS SELECTED THAT MEANS IMAGE IS CHANGED THEN SET isChanged to true
                isChanged = true;
                //MEMBER VARIABLE FOR URI
                mainImageUri= result.getUri();
                //TO SET MAINIAMGE AS THE CROPPED IMAGE
                setupImage.setImageURI(mainImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
