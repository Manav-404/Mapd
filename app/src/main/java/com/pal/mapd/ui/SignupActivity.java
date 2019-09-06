package com.pal.mapd.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.pal.mapd.R;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity implements  View.OnClickListener {

    private TextInputEditText email;
    private TextInputEditText password;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);

        email = findViewById(R.id.email_signup_text);
        password = findViewById(R.id.password_signup_text);
        findViewById(R.id.signup_page_button).setOnClickListener(this);
        findViewById(R.id.login_signup_page_btn).setOnClickListener(this);







    }


    public void registerUser(final String email , String password){


        mAuth.createUserWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){



                    redirectToProfile();

                }else{

                }

            }
        });
    }

    private void redirectToProfile() {

        Intent profile = new Intent(SignupActivity.this , ProfileActivity.class);
        startActivity(profile);
        finish();
    }

    private void redirectToLoginPage() {

        Intent login = new Intent(SignupActivity.this , MainActivity.class);
        startActivity(login);
        finish();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.signup_page_button:{

                if(!TextUtils.isEmpty(email.getText().toString())&& !TextUtils.isEmpty(password.getText().toString())){
                    registerUser(email.getText().toString() , password.getText().toString());
                }else{
                    Toast.makeText(SignupActivity.this, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.login_signup_page_btn:{
                redirectToLoginPage();
                break;
            }


        }

    }


}
