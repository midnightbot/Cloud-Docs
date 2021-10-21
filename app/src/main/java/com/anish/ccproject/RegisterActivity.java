package com.anish.ccproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;
    private FirebaseAuth mAuth;

    private Toolbar mToolbar;

    private ProgressDialog mRegprogress;

    private DatabaseReference mDatabase;
    private DatabaseReference uDatabase;
    private String m_Text = "";
    private TextView terms_conditions;
    private CheckBox terms_check;

    private TextView login_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //toolbar set
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Username");
        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegprogress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance(); // firebase auth

        //android fields
        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(R.id.reg_create_btn);
        terms_conditions = (TextView) findViewById(R.id.terms);
        terms_check = (CheckBox) findViewById(R.id.terms_tick);
        login_page = (TextView) findViewById(R.id.login_page_direct);

        login_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);

            }
        });


        // to view terms and conditions //
        terms_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/a2m-sociofy/home"));
                startActivity(browserIntent);
            }
        });
        //

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getApplicationContext(),"Creating your account",Toast.LENGTH_SHORT).show();

                String display_name = mDisplayName.getEditText().getText().toString();
                String search_name=mDisplayName.getEditText().getText().toString().toLowerCase();

                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();
                Integer length_password = password.length();

                if(!terms_check.isChecked()){

                    Toast.makeText(getApplicationContext(), "Accept the terms and conditions to create your account", Toast.LENGTH_SHORT).show();
                }

                if(length_password<6){
                    Toast.makeText(getApplicationContext(), "Password must be minimum of 6 characters", Toast.LENGTH_SHORT).show();
                }

                if(!TextUtils.isEmpty(display_name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && terms_check.isChecked() && length_password >6){


                    validateUsername(display_name,email,password,search_name);
                }


            }
        });
    }

    private void validateUsername(final String display_name, final String email, final String password,final String search_name) {


        uDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(display_name)){
                    mRegprogress.setTitle("Registering User");
                    mRegprogress.setMessage("Please wait while we create your account!");
                    mRegprogress.setCanceledOnTouchOutside(false);
                    mRegprogress.show();

                    register_user(display_name,email,password,search_name);


                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("Username Taken");

// Set up the input
                    final EditText input = new EditText(RegisterActivity.this);
// Specify the type of input expected; this, for anish, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

// Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m_Text = input.getText().toString();
                            Log.d("LOL","lll"+m_Text);
                            validateUsername(m_Text,email,password,search_name);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                }
            }
            //  virtual me run karu?
            // nhi toh me ek bar vpas naye firebase se link krke try karu? hr voh same account me dusra app banane ka try kar
            //same account ka kuch scene nhi hai me try kkrat hu, t
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void register_user(final String display_name, String email, String password,final String search_name) {


        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    mAuth.getCurrentUser().sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Verify", "Email sent.");
                                        Toast.makeText(RegisterActivity.this,"Email sent, please check your mail",Toast.LENGTH_SHORT).show();

                                        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                                        String uid = current_user.getUid();

                                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                                        String current_user_id = mAuth.getCurrentUser().getUid();

                                        uDatabase.child(display_name).setValue(uid);
                                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                        HashMap<String, String > userMap = new HashMap<>();
                                        userMap.put("name", display_name);
                                       // userMap.put("status","I Love Sociofy :)");
                                        userMap.put("image","default");
                                        //  userMap.put("thumb_image","default");
                                        userMap.put("device_token",deviceToken);
                                        userMap.put("SearchName",search_name);
                                        // userMap.put("comp_image","null");  isko tab add karna jab humko image comp rakhna hoga
                                        // aur agar hum img comp dalenege toh uske liye alag participated ka key ayega
                                        //  userMap.put("shayari_comp","null");
                                        // userMap.put("likes","0");
                                        //   userMap.put("participated","no");
                                        //    userMap.put("referral","0");

                                        mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){
                                                    mRegprogress.dismiss();
                                                    Intent mainIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainIntent);
                                                    finish(); // we dont we user to press back button and come back on this
                                                }
                                            }
                                        });







                                    }
                                    else{

                                        mRegprogress.hide();
                                        Toast.makeText(RegisterActivity.this,"Cannot Sign in now. Try again Later!",Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });



                }else{
                    Toast.makeText(RegisterActivity.this,"Enter a valid mail id",Toast.LENGTH_SHORT).show();
                    Log.d("rr",task.getException().getMessage());
                }
            }});
    }
}
