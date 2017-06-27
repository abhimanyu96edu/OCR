package com.abhimanyu.ocr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hod.api.hodclient.HODApps;
import hod.api.hodclient.HODClient;
import hod.api.hodclient.IHODClientCallback;
import hod.response.parser.HODResponseParser;

public class ImageToText extends AppCompatActivity implements IHODClientCallback {
    String api = "bc7da6ae-8423-49e4-8f41-d7a058d0195b";

    HODClient hodClient;
    HODResponseParser hodParser;
    int controlVariable = 0;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
                String realPath;
                Uri selectedImageURI = data.getData();
                realPath = (getRealPathFromURI(selectedImageURI));
                useHODClient(realPath);

            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", "");
                setResult(2, intent);
                finish();

            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.putExtra("MESSAGE", "");
            setResult(2, intent);
            finish();

        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void useHODClient(String incompleteSearchString) {

        if (controlVariable == 0) {

            hodClient = new HODClient(api, this);
            hodParser = new HODResponseParser();

            String hodApp = HODApps.OCR_DOCUMENT;
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("file", incompleteSearchString);
            controlVariable = 0;
            mProgressDialog = new ProgressDialog(ImageToText.this);
            mProgressDialog.setTitle("Uploading Image");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
            hodClient.PostRequest(params, hodApp, HODClient.REQ_MODE.SYNC);

        } else {
            controlVariable = 2;
        }
    }

    @Override
    public void requestCompletedWithContent(String response) {

        System.out.println(response);

        if (response != null) {
            System.out.println("No Error" + getLocalClassName());

            JsonAnalysis(response);

        } else {
            controlVariable = 0;
            System.out.println("Error");
            Intent intent = new Intent();
            intent.putExtra("MESSAGE", "");
            setResult(2, intent);
            finish();

        }
    }

    @Override
    public void requestCompletedWithJobID(String response) {
        System.out.print(response);

    }

    @Override
    public void onErrorOccurred(String errorMessage) {

    }

    void JsonAnalysis(String responce) {

        try {
            JSONObject jsonRootObject = new JSONObject(responce);
            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("text_block");
            String text = "";
            //Iterate the jsonArray and print the info of JSONObjects
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String name = jsonObject.optString("text").toString();
                text = text + "\n" + name;

            }
            mProgressDialog.cancel();
            Intent intent = new Intent();
            intent.putExtra("MESSAGE", text);
            setResult(2, intent);
            finish();

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

}
