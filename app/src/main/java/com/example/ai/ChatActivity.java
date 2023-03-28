package com.example.ai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private List<ChatMessage> list;
    ChatMessageAdapter chatAdapter;
    ListView chat_listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initview();
    }
    public void returnmain1(View v){
        Intent intent = new Intent(ChatActivity.this,MainActivity.class);
        startActivity(intent);
    }
    public void send(View v){
        ListView chat_listview = (ListView) findViewById(R.id.chat_listview);
        EditText chat_input = (EditText) findViewById(R.id.chat_input_message);
        String send_message = chat_input.getText().toString().trim();
        System.out.println(chat_input.getText());
        if (TextUtils.isEmpty(send_message)) {
            Toast.makeText(ChatActivity.this, "对不起，您还未发送任何消息",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        ChatMessage sendChatMessage = new ChatMessage();
        sendChatMessage.setMessage(send_message);
        sendChatMessage.setDate(new Date());
        sendChatMessage.setType(1);
        list.add(sendChatMessage);
        chatAdapter = new ChatMessageAdapter(list);
        chat_listview.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
        chat_input.setText("");
        
        //聊天实现
        new Thread(new Runnable(){
            @Override
            public void run() {
                TalkUtil util = new TalkUtil();
                String aa= null;
                try {
                    aa = util.getMessage(send_message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ChatMessage chat =new ChatMessage(aa, 0, new Date());
                Message message = new Message();
                message.what = 0x1;
                message.obj = chat;
                handler.sendMessage(message);
            }
        }).start();
    }
    private Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            ChatMessage chatMessage = null;
            if (msg.what == 0x1) {
                if (msg.obj != null) {
                    chatMessage = (ChatMessage) msg.obj;
                }
                // 添加数据到list中，更新数据
                list.add(chatMessage);
                chatAdapter = new ChatMessageAdapter(list);
                chat_listview.setAdapter(chatAdapter);
                chatAdapter.notifyDataSetChanged();
            }
        };
    };
    
    public void initview(){
        chat_listview = (ListView) findViewById(R.id.chat_listview);
        list = new ArrayList<ChatMessage>();
        list.add(new ChatMessage("你好，请和小Q聊天吧!", 0, new Date()));
        chatAdapter = new ChatMessageAdapter(list);
        chat_listview.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
    }
}