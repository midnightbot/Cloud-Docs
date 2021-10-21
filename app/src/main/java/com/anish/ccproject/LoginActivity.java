package com.anish.ccproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jeevandeshmukh.glidetoastlib.GlideToast;

public class LoginActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;

    private Button mLogin_btn;
    private TextView forgot_pass;
    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");


        mAuth = FirebaseAuth.getInstance();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mLoginProgress = new ProgressDialog(this);


        mLoginEmail = (TextInputLayout) findViewById(R.id.login_email);
        mLoginPassword = (TextInputLayout) findViewById(R.id.login_password);
        mLogin_btn = (Button) findViewById(R.id.login_btn);
        forgot_pass = (TextView) findViewById(R.id.forgot);

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));


            }
        });

        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mLoginEmail.getEditText().getText().toString();
                String password = mLoginPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    mLoginProgress.setTitle("Logging In..");
                    mLoginProgress.setMessage("Please wait while we are checking your credentials..");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser(email,password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    if (mAuth.getCurrentUser().isEmailVerified()) {

                        mLoginProgress.dismiss();

                        String current_user_id = mAuth.getCurrentUser().getUid();

                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Oops..Some Error Occurred", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    } else {

                        mLoginProgress.hide();
                        Toast.makeText(LoginActivity.this, "Cannot Sign in now. Verify your mail", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    mLoginProgress.dismiss();
                    //Toast.makeText(LoginActivity.this,"Credentials do not match",Toast.LENGTH_SHORT).show();
                    new GlideToast.makeToast(LoginActivity.this,"Credentials do not match",GlideToast.LENGTHLONG,GlideToast.FAILTOAST,GlideToast.BOTTOM,R.drawable.default_avatar,"#FF0000").show();

                }
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
