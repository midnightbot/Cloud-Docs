package com.anish.ccproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SharedFragment extends Fragment {
    private RecyclerView recyclerView ;
    private DatabaseReference shareRef;
    private DatabaseReference fileRef;
    private FirebaseAuth mAuth;
    private String mCurr;



    public SharedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_shared, container, false);
        mAuth = FirebaseAuth.getInstance();
        mCurr = mAuth.getUid();
        shareRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurr).child("shared");
        fileRef = FirebaseDatabase.getInstance().getReference().child("Documents");
        recyclerView = view.findViewById(R.id.recv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        shareRef.keepSynced(true);
        FirebaseRecyclerAdapter<String, fileholder> adapter = new FirebaseRecyclerAdapter<String, fileholder>(
                String.class,
                R.layout.fileview,
                fileholder.class,
                shareRef
        ) {
            @Override
                protected void populateViewHolder(final fileholder fileholder, String file, int i) {
                final String list_file_id = getRef(i).getKey();
                fileRef.child(list_file_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        char i = 'n';
                        String filename = snapshot.child("filename").getValue().toString();
                        String owner = snapshot.child("owned").getValue().toString();
                        if (snapshot.hasChild("Text")){
                            i='d';
                        }
                        fileholder.setFilename(filename);
                        fileholder.setUsername(owner);
                        final char finalI = i;
                        fileholder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle args = new Bundle();
                                args.putString("fileid",list_file_id);
                                args.putChar("docType", finalI);
                                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                                bottomSheet .setArguments(args);
                                bottomSheet.show(getFragmentManager(),
                                        "ModalBottomSheet");

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

        return view;
    }

    public static class fileholder  extends  RecyclerView.ViewHolder{
        View mView;


        public fileholder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setFilename(String filename) {
            TextView name = mView.findViewById(R.id.filename);
            name.setText(filename);
        }
        public void setUsername(final String username){
            final TextView name = mView.findViewById(R.id.share);
            FirebaseDatabase.getInstance().getReference().child("Users").child(username).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                  String userr_name = snapshot.child("name").getValue().toString();
                    String user = "shared by: \n" + userr_name;
                    name.setText(user);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}