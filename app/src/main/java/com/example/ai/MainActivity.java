package com.example.ai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void startchat(View v) throws JSONException {

        Intent intent = new Intent(MainActivity.this,ChatActivity.class);
        startActivity(intent);
    }
    public void selfinf(View v){
//        Toast.makeText(this, "恭喜，登陆成功",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this,SelfActivity.class);
        startActivity(intent);

    }
    public void camera(View v)  {

        Intent intent = new Intent(MainActivity.this,CameraActivity.class);
        startActivity(intent);
    }

}