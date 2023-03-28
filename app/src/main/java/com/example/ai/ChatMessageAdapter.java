package com.example.ai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.util.List;


public class ChatMessageAdapter extends BaseAdapter {
    private List<ChatMessage> list;
    ChatMessageAdapter(List<ChatMessage> list){
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = list.get(position);
        // 如果是接收消息：0，发送消息：1
        if (chatMessage.getType() == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessage = list.get(position);

        SharedPreferences preferences;
        preferences = 	ContextApplication.getAppContext().getSharedPreferences("data",0);
        String username = preferences.getString("username","");

        SharedPreferences sharedPreferences=ContextApplication.getAppContext().getSharedPreferences("imgdata", Context.MODE_PRIVATE);
        String imageString=sharedPreferences.getString("image", "");
        byte[] byteArray= Base64.decode(imageString, Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
        Bitmap bitmap= BitmapFactory.decodeStream(byteArrayInputStream);

        if (convertView == null) {
            ViewHolder viewHolder = null;
            // 通过ItemType加载不同的布局
            if (getItemViewType(position) == 0) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_left, null);
                viewHolder = new ViewHolder();
                viewHolder.chat_time = (TextView) convertView.findViewById(R.id.chat_left_time);
                viewHolder.chat_message = (TextView) convertView.findViewById(R.id.chat_left_message);
            } else {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_right, null);
                viewHolder = new ViewHolder();
                viewHolder.chat_time = (TextView) convertView.findViewById(R.id.chat_right_time);
                viewHolder.chat_message = (TextView) convertView.findViewById(R.id.chat_right_message);
                viewHolder.chat_right_name = (TextView) convertView.findViewById(R.id.chat_right_name);
                viewHolder.chat_right_image = (ImageView) convertView.findViewById(R.id.chat_right_image);
                viewHolder.chat_right_name.setText(username);
                viewHolder.chat_right_image.setImageBitmap(bitmap);
            }
            convertView.setTag(viewHolder);
        }
        // 设置数据
        ViewHolder vh = (ViewHolder) convertView.getTag();
        vh.chat_time.setText(DateUtil.dateToString(chatMessage.getDate()));
        vh.chat_message.setText(chatMessage.getMessage());
        return convertView;
    }
    private class ViewHolder {
        private TextView chat_time, chat_message,chat_right_name;
        private ImageView chat_right_image;
    }
}
