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

public class RegisterActivity extends AppCompatActivity {
private EditText reg_email_field;
private EditText reg_pass_field;
private EditText reg_confirm_pass_field;
private Button reg_btn;
private Button reg_login_btn;
private ProgressBar reg_progress;
private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        reg_email_field = findViewById(R.id.reg_email);
        reg_pass_field = findViewById(R.id.reg_password);
        reg_confirm_pass_field = findViewById(R.id.reg_confirm_password);
        reg_login_btn = findViewById(R.id.reg_createaccount_btn);
        reg_btn = findViewById(R.id.alreadyacc_btn);
        reg_progress = findViewById(R.id.regloginprogressBar);

reg_login_btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String email = reg_email_field.getText().toString();
        String pass = reg_pass_field.getText().toString();
        String confirmpass = reg_confirm_pass_field.getText().toString();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirmpass)){

            if(pass.equals(confirmpass)){
mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()){
sendtosetupactivity();
            Toast.makeText(RegisterActivity.this,"done",Toast.LENGTH_SHORT).show();
        }else{

            //TO GET ERROR MESSAGE AND SHOW TO USER
            String errormessage = task.getException().getMessage();
            Toast.makeText(RegisterActivity.this,"Error : " + errormessage,Toast.LENGTH_SHORT).show();
        }
    }
});
            }else{
                Toast.makeText(RegisterActivity.this,"Please enter valid email and password or Password and Confirm password didn't match",Toast.LENGTH_SHORT).show();
            }

        }
    }
});


reg_btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent regintent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(regintent);
        finish();
    }
});
    }

    private void sendtosetupactivity() {
        Intent setupIntent = new Intent(RegisterActivity.this,SetuoActivity.class);
        startActivity(setupIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            senttoMain();
        }
    }

    private void senttoMain() {

        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
