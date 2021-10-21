package com.anish.ccproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileFragment extends Fragment {


    private TextView user_name;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private String mUserId;
    private TextView encd;
    private TextView userowned;
    private TextView userrecvddocs;
    private Button logout;
    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        user_name = (TextView) view.findViewById(R.id.user_profile_name);
        encd = (TextView) view.findViewById(R.id.encoding_demo);
        userowned = (TextView) view.findViewById(R.id.user_owned);
        userrecvddocs = (TextView) view.findViewById(R.id.user_recvd);
        logout = (Button) view.findViewById(R.id.log_out);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent startIntent = new Intent(getContext(), RegisterActivity.class);
                startActivity(startIntent);
                //finish();
            }
        });

        encd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),Caesar.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mUserId = FirebaseAuth.getInstance().getUid();
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUserId);
        }
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //mUserRef.keepSynced(true);
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String namee = snapshot.child("name").getValue().toString();
                if(snapshot.child("owned")!= null)
                {
                    int owned_document = (int) snapshot.child("owned").getChildrenCount();
                    userowned.setText("Created Documents : "+ owned_document);
                }
                else{
                    userowned.setText("Created Documents : 0");
                }
                if(snapshot.child("shared")!=null){
                    int shared_docs = (int) snapshot.child("shared").getChildrenCount();
                    userrecvddocs.setText("Documents shared to "+ namee+" "+": "+shared_docs);
                }
                else{
                    userrecvddocs.setText("Documents shared to "+ namee+" "+": 0");
                }

                user_name.setText("Userid : " + namee);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}