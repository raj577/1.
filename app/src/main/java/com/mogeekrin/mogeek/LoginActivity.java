package com.mogeekrin.mogeek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
private Button loginBtn;
    private Button loginRegBth;
    private EditText loginEmailtext;
    private EditText loginPassBtn;
    private ProgressBar loginProgressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
mAuth = FirebaseAuth.getInstance();
        loginBtn = findViewById(R.id.login_btn);
        loginRegBth = findViewById(R.id.login_reg_btn);
        loginEmailtext = findViewById(R.id.email);
        loginPassBtn = findViewById(R.id.password);
        loginProgressBar = findViewById(R.id.loginprogressBar);

loginBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {


        String loginEmail = loginEmailtext.getText().toString();
        String loginPass = loginPassBtn.getText().toString();

//TO CHECK IF THE EMAIL AND PASSWORD `IS NOT EMPTY
        if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)){
            loginProgressBar.setVisibility(View.VISIBLE);

            //TO CHECK IF LOGIN IS SUCCESFUL OR NOT
            mAuth.signInWithEmailAndPassword(loginEmail,loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    //If the login is sussefull then
                    if (task.isSuccessful()){

                  sendToMain();

                    }
                   //if login not ssufull then
                    else {
                        loginProgressBar.setVisibility(View.INVISIBLE);
                        //TO GET THE ERROR MESSAGE
String errorMessage  = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error : " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
});


    }

    @Override
    protected void onStart() {
        super.onStart();

        //To check if the user is already logged in

        sendToMain();
    }

    private void sendToMain() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
