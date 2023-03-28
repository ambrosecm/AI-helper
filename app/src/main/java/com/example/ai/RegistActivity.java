package com.example.ai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
    }
    public void sureregist(View v){
        EditText n=findViewById(R.id.editTextTextPersonName2);
        EditText p=findViewById(R.id.editTextTextPassword2);
        EditText sp=findViewById(R.id.editTextTextPassword3);
        if(p.getText().toString().equals(sp.getText().toString())){

            MyHelper helper = new MyHelper(RegistActivity.this);
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", n.getText().toString());
            values.put("password", p.getText().toString());
            long id = db.insert("users",null,values);
            db.close();
            Toast.makeText(this, "注册成功",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegistActivity.this,LoginActivity.class);
            startActivity(intent);
        }else {
            Toast.makeText(this, "请输入两次相同的密码",Toast.LENGTH_SHORT).show();
        }

    }

}