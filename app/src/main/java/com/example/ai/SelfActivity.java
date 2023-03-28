package com.example.ai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.Date;

public class SelfActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    public static final String action = "NI";
    ImageView chatimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self);
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        String name = pref.getString("username","");
        TextView v2=findViewById(R.id.textView2);
        v2.setText(name);

        SharedPreferences sharedPreferences=getSharedPreferences("imgdata", Context.MODE_PRIVATE);
        String imageString=sharedPreferences.getString("image", "");
        byte[] byteArray= Base64.decode(imageString, Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
        Bitmap bitmap=BitmapFactory.decodeStream(byteArrayInputStream);
        ImageView imageView1 = (ImageView) findViewById(R.id.imageView);
        imageView1.setImageBitmap(bitmap);


    }
    public void returnmain(View v){
        Intent intent = new Intent(SelfActivity.this,MainActivity.class);
        startActivity(intent);
    }
    public void changename(View v){
        Intent intent = new Intent(SelfActivity.this,ChangeNActivity.class);
        TextView v2=findViewById(R.id.textView2);
        intent.putExtra("name", v2.getText().toString());
        startActivityForResult(intent, 666);
    }

    public void changeimg(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            Bitmap bm = null;
            try {
                bm= BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenWidth=dm.widthPixels;
            ImageView imageView1 = (ImageView) findViewById(R.id.imageView);
            if(bm.getWidth()<=screenWidth){
                imageView1.setImageBitmap(bm);
            }else{
                bm=Bitmap.createScaledBitmap(bm, screenWidth, bm.getHeight()*screenWidth/bm.getWidth(), true);
                imageView1.setImageBitmap(bm);
            }

            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
            byte[] byteArray=byteArrayOutputStream.toByteArray();
            String imageString=new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
            SharedPreferences sharedPreferences=getSharedPreferences("imgdata", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("image", imageString);
            editor.commit();



        }else if(requestCode == 666 && resultCode == 888){
            String newname = data.getStringExtra("newname");
            TextView v2=findViewById(R.id.textView2);
            View view = View.inflate(this, R.layout.activity_right, null);
            TextView tv=view.findViewById(R.id.chat_right_name);
            tv.setText(newname);
            v2.setText(newname);

            //存储数据
            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
            editor.putString("username",newname);
            editor.commit();


        }


    }

}