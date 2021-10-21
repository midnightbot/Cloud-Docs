package com.anish.ccproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.database.ServerValue.*;

public class TextEditor extends AppCompatActivity {
    private Toolbar toolbar;
    private Caesar crypt;
    private LinearLayout typingstatus;
    private String FileId,DocContent,TypingUser,mUser,mCurr;
    private EditText doc;
    private TextView typetext;
    private DatabaseReference FileRef,OnlineRef;
    private RecyclerView recv;
    private Button sharetbn,accssbtn,savebtn,closebtn,writebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);

        crypt = new Caesar();

        long now = System.currentTimeMillis();
        SimpleDateFormat sfd = new SimpleDateFormat("ss");
        long l = Long.parseLong(sfd.format(new Date(now)));
        Log.d("rautnik",sfd.format(new Date(now)));

        final Handler handler = new Handler();
        final int delay = 3000; // 1000 milliseconds == 1 second
        handler.postDelayed(new Runnable() {
                public void run() {

                    save(); // Do your work here
                    handler.postDelayed(this, delay);
                }
            }, delay);


        FileId = getIntent().getExtras().getString("fileid");
        toolbar = findViewById(R.id.mytool);
        sharetbn = findViewById(R.id.sharebtn);
        typingstatus = findViewById(R.id.typestatus);
        accssbtn = findViewById(R.id.viewaccsbtn);
        closebtn = findViewById(R.id.closebtn);
        writebtn = findViewById(R.id.write);
        typetext = findViewById(R.id.typetext);
        savebtn= findViewById(R.id.save);
        recv = findViewById(R.id.recv);
        recv.setHasFixedSize(true);
        recv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        setSupportActionBar(toolbar);

        doc = findViewById(R.id.document);
        doc.setEnabled(false);
        mCurr = FirebaseAuth.getInstance().getUid();

        sharetbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent share = new Intent(TextEditor.this,SharedActivity.class);
                share.putExtra("fileid",FileId);
                startActivity(share);
            }
        });
        accssbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent share = new Intent(TextEditor.this,ShareAccess.class);
                share.putExtra("fileid",FileId);
                startActivity(share);
            }
        });
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        writebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writebtn.setVisibility(View.GONE);
                savebtn.setVisibility(View.VISIBLE);
                doc.setEnabled(true);
                FileRef.child("type").setValue(mUser);

            }
        });
        TextView auto = findViewById(R.id.auto);
        auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent share = new Intent(TextEditor.this,Autosave.class);
                share.putExtra("fileid",FileId);
                startActivity(share);
            }
        });
        FirebaseDatabase.getInstance().getReference().child("Users").child(mCurr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser = snapshot.child("name").getValue().toString();
                FileRef.child("online").child(mUser).setValue(mUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FileRef = FirebaseDatabase.getInstance().getReference().child("Documents").child(FileId);
        FileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Text")) {
                    String firedb = snapshot.child("Text").getValue().toString();
                    DocContent = crypt.decrypt(firedb);
                }else {
                    DocContent = "";
                }

                doc.setText(DocContent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("type")) {
                    TypingUser = snapshot.child("type").getValue().toString();
                    typingstatus.setVisibility(View.VISIBLE);
                    String te = TypingUser + " is editing..";
                    typetext.setText(te);
                    if (TypingUser != mUser){
                        Log.d("pagal",TypingUser+mUser);
                        writebtn.setEnabled(false);
                    }else {
                        Log.d("pagal","sa");

                        writebtn.setEnabled(true);

                    }
                }else {
                    typingstatus.setVisibility(View.GONE);

                    writebtn.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();

            }
        });


        OnlineRef = FileRef.child("online");

        FirebaseRecyclerAdapter<String, fileholder> adapter = new FirebaseRecyclerAdapter<String, fileholder>(
                String.class,
                R.layout.onlinestatus,
                fileholder.class,
                OnlineRef
        ) {
            @Override
            protected void populateViewHolder(final fileholder fileholder, final String file, int i) {
                final String list_file_id = getRef(i).getKey();
                Log.d("chuu",list_file_id);
                fileholder.setImage(list_file_id.charAt(0));



            }
        };
        recv.setAdapter(adapter);
    }

    private void save() {
        writebtn.setVisibility(View.VISIBLE);
        savebtn.setVisibility(View.GONE);
        doc.setEnabled(false);
        String temp = doc.getText().toString();
        String encodedtemp = crypt.encrypt(temp);
        FileRef.child("Text").setValue(encodedtemp);
        FileRef.child("type").removeValue();
    }

    @Override
    public void onBackPressed() {
        String temp = doc.getText().toString();
        String encodedtemp = crypt.encrypt(temp);

        FileRef.child("Text").setValue(encodedtemp);
        FileRef.child("type").removeValue();
        FileRef.child("online").child(mUser).removeValue();
        super.onBackPressed();
    }

    public static class fileholder  extends  RecyclerView.ViewHolder{
        View mView;
        CircleImageView icon;


        public fileholder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            icon = mView.findViewById(R.id.onlineicon);

        }
        public void setImage(char c){
            switch (c){
                case 'a':
                    icon.setImageResource(R.drawable.a);
                    break;
                case 'b':
                    icon.setImageResource(R.drawable.b);
                    break;
                case 'c':
                    icon.setImageResource(R.drawable.c);
                    break;
                case 'd':
                    icon.setImageResource(R.drawable.d);
                    break;
                case 'e':
                    icon.setImageResource(R.drawable.e);
                    break;
                case 'f':
                    icon.setImageResource(R.drawable.f);
                    break;
                case 'g':
                    icon.setImageResource(R.drawable.g);
                    break;
                case 'h':
                    icon.setImageResource(R.drawable.h);
                    break;
                case 'i':
                    icon.setImageResource(R.drawable.i);
                    break;
                case 'j':
                    icon.setImageResource(R.drawable.j);
                    break;
                case 'k':
                    icon.setImageResource(R.drawable.k);
                    break;
                case 'l':
                    icon.setImageResource(R.drawable.l);
                    break;
                case 'm':
                    icon.setImageResource(R.drawable.m);
                    break;
                case 'n':
                    icon.setImageResource(R.drawable.n);
                    break;
                case 'o':
                    icon.setImageResource(R.drawable.o);
                    break;
                case 'p':
                    icon.setImageResource(R.drawable.p);
                    break;
                case 'q':
                    icon.setImageResource(R.drawable.q);
                    break;
                case 'r':
                    icon.setImageResource(R.drawable.r);
                    break;
                case 's':
                    icon.setImageResource(R.drawable.s);
                    break;
                case 't':
                    icon.setImageResource(R.drawable.t);
                    break;
                case 'u':
                    icon.setImageResource(R.drawable.u);
                    break;
                case 'v':
                    icon.setImageResource(R.drawable.v);
                    break;
                case 'w':
                    icon.setImageResource(R.drawable.w);
                    break;
                case 'x':
                    icon.setImageResource(R.drawable.x);
                    break;
                case 'y':
                    icon.setImageResource(R.drawable.y);
                    break;
                case 'z':
                    icon.setImageResource(R.drawable.z);
                    break;
                    }
        }

    }

}