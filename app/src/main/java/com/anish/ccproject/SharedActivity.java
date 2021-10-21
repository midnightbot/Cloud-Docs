package com.anish.ccproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SharedActivity extends AppCompatActivity {
    private ListView listView;
    private SearchView searchView;
    private ArrayList<String> userList;
    private ArrayAdapter<String> adapter;
    private String FileId;
    private DatabaseReference userId,shareId,UserDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared);
        FileId = getIntent().getExtras().getString("fileid");
        listView = findViewById(R.id.list);
        searchView = findViewById(R.id.search);
        userList = new ArrayList<String>();
        UserDB = FirebaseDatabase.getInstance().getReference().child("Users");
        userId = FirebaseDatabase.getInstance().getReference().child("Username");
        shareId = FirebaseDatabase.getInstance().getReference().child("Documents").child(FileId);
        userId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    userList.add(ds.getKey());
                }

                shareId.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("shared")){
                            for (DataSnapshot ds: snapshot.child("shared").getChildren()){
                                userList.remove(ds.getKey());
                            }
                            Populate();
                        }else{
                            Populate();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void Populate() {
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,userList);
        listView.setAdapter(adapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String selectedItem = (String) adapterView.getItemAtPosition(i);
                userId.child(selectedItem).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String shareuserId = snapshot.getValue().toString();
                        UserDB.child(shareuserId).child("shared").child(FileId).setValue(FileId);
                        shareId.child("shared").child(selectedItem).setValue(selectedItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }


}