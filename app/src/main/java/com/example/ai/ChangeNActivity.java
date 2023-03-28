package com.example.ai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ChangeNActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_n);
        String name = getIntent().getStringExtra("name");
        EditText ed=findViewById(R.id.editTextTextPersonName);
        ed.setText(name);
    }
    public void sure(View v){
        Intent intent = new Intent();
        EditText ed=findViewById(R.id.editTextTextPersonName);
        intent.putExtra("newname", ed.getText().toString());
        setResult(888, intent);
        finish();
    }
}