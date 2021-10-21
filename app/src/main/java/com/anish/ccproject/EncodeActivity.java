package com.anish.ccproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.internal.Constants;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncodeActivity extends AppCompatActivity {

    private EditText input;
    private Button encodeButton;
    private TextView encdtext,decdtext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);
        input = (EditText) findViewById(R.id.mytext);
        encdtext = (TextView) findViewById(R.id.encoded_text);
        decdtext = (TextView) findViewById(R.id.decoded_text);

        encodeButton = (Button) findViewById(R.id.encode_button);

        encodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Decoding","started decoding");

                String input_string = input.getText().toString();

                Log.d("Encoding","started encoding");

          String encoded = Base64.encodeToString(input_string.getBytes(),Base64.DEFAULT);

                encdtext.setText(encoded);


                ///
                byte[] data = Base64.decode(encoded, Base64.DEFAULT);
                try {
                    String text1 = new String(data, "UTF-8");
                    decdtext.setText(text1);
                    Log.d("Decoding","ended decoding");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }
        });



    }

}