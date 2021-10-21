package com.anish.ccproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Caesar extends AppCompatActivity {
    private EditText input;
    private Button encodeButton;
    private TextView encdtext,decdtext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caesar);
        input = (EditText) findViewById(R.id.mytext);
        encdtext = (TextView) findViewById(R.id.encoded_text);
        decdtext = (TextView) findViewById(R.id.decoded_text);

        encodeButton = (Button) findViewById(R.id.encode_button);

        encodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_string = input.getText().toString();
                Log.d("Encoding","started encoding");
                String encode = encrypt(input_string);
                encdtext.setText(encode);
                decdtext.setText(decrypt(encode));



            }
        });


    }
    public String encrypt(String input){
        int onethird = input.length()/3;
        int twothird = 2*onethird;

        String a = encrypter(input.substring(0,onethird),3);
        String b = encrypter(input.substring(onethird,twothird),4);
        String c =encrypter(input.substring(twothird),5);
        Log.d("Encoding","ended encoding");
        return a+b+c;
    }
    public String decrypt(String input){

        int onethird = input.length()/3;
        int twothird = 2*onethird;
        String a = decrypter(input.substring(0,onethird),3);
        String b = decrypter(input.substring(onethird,twothird),4);
        String c =decrypter(input.substring(twothird),5);
        Log.d("Decoding","ended decoding");

        return a+b+c;
    }
    protected String encrypter(String inputString, int key){
        StringBuffer output;
        Character charac;
        int previousAscii,newAscii;
        output = new StringBuffer();

        for(int i=0;i<inputString.length();i++){
            charac = inputString.charAt(i);
            if(charac.equals(' ')){
                output.append(Character.toString(charac));
                continue;
            }
            if(!Character.isLetter(charac)){
                output.append(Character.toString(charac));
                continue;
            }
            previousAscii = (int)charac;
            newAscii = previousAscii + key;
            if(newAscii > 90 && Character.isUpperCase(charac) || newAscii > 122){
                newAscii -= 26;
            }
            output.append(Character.toString((char)newAscii));
        }
        return String.valueOf(output);
    }

    protected String decrypter(String inputString, int key){
        StringBuffer output;
        Character charac;
        int previousAscii,newAscii;
        output = new StringBuffer();

        for(int i=0;i<inputString.length();i++){
            charac = inputString.charAt(i);
            if(charac.equals(' ')){
                output.append(Character.toString(charac));
                continue;
            }
            if(!Character.isLetter(charac)){
                output.append(Character.toString(charac));
                continue;
            }
            previousAscii = (int)charac;
            newAscii = previousAscii - key;
            if(newAscii < 65 && Character.isUpperCase(charac) || newAscii < 97){
                newAscii += 26;
            }
            output.append(Character.toString((char)newAscii));
        }
        return String.valueOf(output);
    }
}