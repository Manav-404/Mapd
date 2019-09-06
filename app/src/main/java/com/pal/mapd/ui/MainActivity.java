package com.pal.mapd.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pal.mapd.R;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
   private TextInputEditText email;
   private  TextInputEditText password;
   private MaterialButton login;
   private MaterialButton signup;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        email=findViewById(R.id.email_input_edit_text);
        password =findViewById(R.id.password_input_edit_text);
        login= findViewById(R.id.login_btn);
        signup= findViewById(R.id.sign_button);
        findViewById(R.id.login_btn).setOnClickListener(this);
        findViewById(R.id.sign_button).setOnClickListener(this);




    }

    public void signIn(){
        String email_=email.getText().toString();
        String password_ = password.getText().toString();
        if(!TextUtils.isEmpty(email_)&& !TextUtils.isEmpty(password_)){
            mAuth.signInWithEmailAndPassword(email_ , password_).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                      Intent main = new Intent(MainActivity.this ,AppPageActivity.class);
                      startActivity(main);
                      finish();
                    }

                }
            });
        }

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_btn:{

                signIn();

                break;
            }

            case R.id.sign_button:{
                sendToSignUp();
                break;
            }
        }
    }

    private void sendToSignUp() {
        Intent sign_up = new Intent(MainActivity.this , SignupActivity.class);
        startActivity(sign_up);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        if(mUser!=null){
            Intent main = new Intent(MainActivity.this , AppPageActivity.class);
            startActivity(main);
            finish();
        }
    }
}
