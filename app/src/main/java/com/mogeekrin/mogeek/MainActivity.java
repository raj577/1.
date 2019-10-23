package com.mogeekrin.mogeek;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
private Toolbar mainToolbar;
private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
mainToolbar = findViewById(R.id.main_toolbar);
setSupportActionBar(mainToolbar);
getSupportActionBar().setTitle("MoGeek... Be Smart");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            sendToLogin();
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_logout_btn :

                logout();
            return true;

            case R.id.settings_btn :
                Intent setupintent = new Intent(MainActivity.this,SetuoActivity.class);
                startActivity(setupintent);
                return true;

            default :
                return false;
        }

    }


    private void sendToLogin() {

        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();


    }


    private void logout() {
mAuth.signOut();
sendToLogin();

    }
}
