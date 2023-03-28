package com.example.ai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void click(View v){
        EditText username=findViewById(R.id.editTextTextPersonName);
        EditText password=findViewById(R.id.editTextTextPassword);
        String name = null;
        String p = null;
        System.out.println(username.getText().toString());
        MyHelper helper = new MyHelper(LoginActivity.this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("users", null, "username=?", new String[]{1+""},null, null, null);
        if (cursor.getCount() != 0){
            while (cursor.moveToNext()){
                name = cursor.getString(cursor.getColumnIndex("username"));
                p = cursor.getString(cursor.getColumnIndex("password"));
            }
        }
        cursor.close();
        db.close();

        if(username.getText().toString().equals(name)&&password.getText().toString().equals(p)){
            Toast.makeText(this, "登陆成功",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "密码错误或该账号不存在",Toast.LENGTH_SHORT).show();
        }
    }
    public void regist(View v){
        new MyHelper(this);
        Intent intent = new Intent(LoginActivity.this,RegistActivity.class);
        startActivity(intent);
    }

}