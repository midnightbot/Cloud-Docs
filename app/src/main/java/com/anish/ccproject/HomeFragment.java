package com.anish.ccproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import at.markushi.ui.CircleButton;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {
    private FirebaseStorage storage;
    private FloatingActionMenu menu;
    private RecyclerView recyclerView ;
    private DatabaseReference mDocs,mUser,shareRef,fileRef;
    private FirebaseAuth mAuth;
    private String mUserid;
    private Uri fileUri;

    public HomeFragment() {
        // Required empty public constructor
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.fragment_home, container, false);

       storage = FirebaseStorage.getInstance();
       mDocs = FirebaseDatabase.getInstance().getReference().child("Documents");
       mUser = FirebaseDatabase.getInstance().getReference().child("Users");
       FloatingActionButton btn = view.findViewById(R.id.attach);
       FloatingActionButton crt = view.findViewById(R.id.create);
       mAuth = FirebaseAuth.getInstance();
       mUserid = mAuth.getUid();
        shareRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUserid).child("owned");
        fileRef = FirebaseDatabase.getInstance().getReference().child("Documents");
        menu = view.findViewById(R.id.menu);
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
            protected void populateViewHolder(final fileholder fileholder, final String file, int i) {
                final String list_file_id = getRef(i).getKey();
                Log.d("chuu",list_file_id);
                fileRef.child(list_file_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        char i='n';
                        String filename = snapshot.child("filename").getValue(String.class);
                        if (snapshot.hasChild("Text")){
                            i='d';
                        }
                        fileholder.setFilename(filename);
                        fileholder.setUsername();
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





       btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               menu.close(true);
               Intent galleryIntent = new Intent();
               galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
               galleryIntent.setType("*/*");
               startActivityForResult(galleryIntent, 1);
           }
       });
        crt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.close(true);
                askname();
            }
        });
       return view;
    }

    private void askname() {
        final EditText taskEditText = new EditText(getActivity());
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Create a new file")
                .setMessage("Enter the name of file")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String filename = String.valueOf(taskEditText.getText());
                        final String key = mUser.push().getKey();

                        mUser.child(mUserid).child("owned").child(key).setValue(key).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mDocs.child(key).child("filename").setValue(filename);
                                mDocs.child(key).child("owned").setValue(mUserid);
                                mDocs.child(key).child("Text").setValue(" ");
                                Intent share = new Intent(getActivity(),TextEditor.class);
                                share.putExtra("fileid",key);
                                startActivity(share);
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            Log.d("fileUri",fileUri.getPath());
            final String nameoffile = fileUri.getLastPathSegment();

            final StorageReference riversRef = storage.getReference().child("Documents/"+nameoffile);
            riversRef.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String down = uri.toString();
                                final String key = mUser.push().getKey();

                                mUser.child(mUserid).child("owned").child(key).setValue(key).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDocs.child(key).child("filename").setValue(nameoffile);
                                        mDocs.child(key).child("owned").setValue(mUserid);
                                        mDocs.child(key).child("url").setValue(down);
                                    }
                                });

                            }
                        });
                    }
                }
            });

// Register observers to listen for when the download is done or if it fails

        }
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
        public void setUsername(){
            TextView name = mView.findViewById(R.id.share);
            name.setText("");
        }
    }

}