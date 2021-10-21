package com.anish.ccproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private String mUserId;

    private Toolbar home;
    private TextView user_name;
    private ImageView rate;
    private ImageView chatbot;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        home = findViewById(R.id.home_toolbar);
        user_name = (TextView) findViewById(R.id.user_name);
        rate = (ImageView) findViewById(R.id.smiley_image);
        chatbot = (ImageView) findViewById(R.id.question_image);

        chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChatbotActivity.class);

                startActivity(intent);
            }
        });


        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                View view_bottom = getLayoutInflater().inflate(R.layout.activity_rate_app,null);
                com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                bottomSheetDialog.setContentView(view_bottom);
                BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view_bottom.getParent());
                mBehavior.setPeekHeight(500);
                final ImageView img1 = (ImageView) view_bottom.findViewById(R.id.sad);
                final ImageView img2 = (ImageView) view_bottom.findViewById(R.id.equal);
                final ImageView img3 = (ImageView) view_bottom.findViewById(R.id.smile);
                final TextView review_txt = (TextView) view_bottom.findViewById(R.id.review_text);




                bottomSheetDialog.show();


                img1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img1.setImageResource(R.drawable.mesadcolored);
                        img2.setImageResource(R.drawable.meequal);
                        img3.setImageResource(R.drawable.mesmile);
                        review_txt.setText("Not Satisying");
                        review_txt.setTextColor(Color.RED);
                    }
                });
                img2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img2.setImageResource(R.drawable.meequalcolored);
                        img1.setImageResource(R.drawable.mesad);
                        img3.setImageResource(R.drawable.mesmile);
                        review_txt.setText("Ok");
                        review_txt.setTextColor(Color.GRAY);
                    }
                });

                img3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        img1.setImageResource(R.drawable.mesad);
                        img2.setImageResource(R.drawable.meequal);
                        img3.setImageResource(R.drawable.mesmilecolored);
                        review_txt.setText("Satisying");
                        review_txt.setTextColor(Color.GREEN);
                    }
                });


            }
        });

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mUserId = FirebaseAuth.getInstance().getUid();
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUserId);
        }



        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String namee = snapshot.child("name").getValue().toString();
                user_name.setText("userid :" + namee);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Log.d("findIt Bro",mUserId);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_shareddocs);
        bottomNav.bringToFront();
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contanier, new SharedFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_yourdocs:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_shareddocs:
                            selectedFragment = new SharedFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                            break;

                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_contanier, selectedFragment).commit();

                    return true;
                }
            };


}
