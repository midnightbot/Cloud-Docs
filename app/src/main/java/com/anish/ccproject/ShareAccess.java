package com.anish.ccproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShareAccess extends AppCompatActivity {
    private RecyclerView recyclerView;

    public DatabaseReference fileRef;
    private FirebaseAuth mAuth;
    private String mCurr;
    private static String FileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileId = getIntent().getExtras().getString("fileid");

        setContentView(R.layout.activity_share_access);
        recyclerView = findViewById(R.id.recv);
        mAuth = FirebaseAuth.getInstance();
        mCurr = mAuth.getUid();
        fileRef = FirebaseDatabase.getInstance().getReference().child("Documents").child(FileId).child("shared");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileRef.keepSynced(true);
        FirebaseRecyclerAdapter<String, fileholder> adapter = new FirebaseRecyclerAdapter<String, fileholder>(
                String.class,
                R.layout.userview,
                fileholder.class,
                fileRef
        ) {
            @Override
            protected void populateViewHolder(final fileholder fileholder, String file, int i) {
                final String list_file_id = getRef(i).getKey();
                fileholder.setUsername(list_file_id);
                fileholder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(ShareAccess.this)
                                .setTitle("Title")
                                .setMessage("Do you really want to remove access to this user?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        fileholder.removeAccess(list_file_id);
                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);

    }
    public static class fileholder  extends  RecyclerView.ViewHolder{
        View mView;


        public fileholder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setUsername(String username){
            TextView name = mView.findViewById(R.id.username);

            name.setText(username);
        }

        public void removeAccess(String list_file_id) {
            FirebaseDatabase.getInstance().getReference().child("Documents").child(FileId).child("shared").child(list_file_id).removeValue();
            FirebaseDatabase.getInstance().getReference().child("Username").child(list_file_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String YouserId = snapshot.getValue().toString();
                    FirebaseDatabase.getInstance().getReference().child("Users").child(YouserId).child("shared").child(FileId).removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

}