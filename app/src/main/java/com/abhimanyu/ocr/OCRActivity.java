package com.abhimanyu.ocr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

public class OCRActivity extends Activity {

    ImageButton searchView, uploadImage;
    AutoCompleteTextView searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        searchEditText = (AutoCompleteTextView) findViewById(R.id.editText);
        searchView = (ImageButton) findViewById(R.id.searchView);

        searchView.setBackgroundColor(Color.TRANSPARENT);
        searchView.setColorFilter(Color.parseColor("#FF4081"));
        uploadImage = (ImageButton) findViewById(R.id.Imageupload);
        uploadImage.setBackgroundColor(Color.TRANSPARENT);
        uploadImage.setColorFilter(Color.parseColor("#FF4081"));

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();
                Intent intent = new Intent(OCRActivity.this, ImageToText.class);
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            try {
                String message = data.getStringExtra("MESSAGE");
                ///////////////////////////////////      FINAL OCR RESULT       ///////////////////////////
                searchEditText.setText(message);
            } catch (Exception e) {
                searchEditText.setText("");
            }
        }
    }

    void vibrate() {
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(60);
    }
}
