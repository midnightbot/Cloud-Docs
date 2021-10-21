package com.anish.ccproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.bottomsheet,
                container, false);
        Bundle mArgs = getArguments();
        final String FileId = mArgs.getString("fileid");
        final char doctype = mArgs.getChar("docType");

        LinearLayout share_btn = v.findViewById(R.id.share);
        LinearLayout accs_btn = v.findViewById(R.id.access);
        LinearLayout open_btn = v.findViewById(R.id.view);

        open_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d("Nihal",FileId);
                if (doctype == 'd'){
                    Intent share = new Intent(getActivity(),Autosave.class);
                    share.putExtra("fileid",FileId);
                    startActivity(share);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Documents").child(FileId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String url = snapshot.child("url").getValue().toString();
                            Intent share = new Intent(getActivity(),LoadWebViewActivity.class);
                            share.putExtra("website-link",url);
                            startActivity(share);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d("Nihal",FileId);
                Intent share = new Intent(getActivity(),SharedActivity.class);
                share.putExtra("fileid",FileId);
                startActivity(share);
            }
        });
        accs_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent share = new Intent(getActivity(),ShareAccess.class);
                share.putExtra("fileid",FileId);
                startActivity(share);
            }
        });


        return v;
    }
}
