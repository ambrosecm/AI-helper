package com.example.ai;

import java.util.Date;

public class ChatMessage {

    private String name;// 姓名
    private String message;// 消息
    private int type;// 类型：0.发送者 1.接受者
    private Date date;// 时间

    public ChatMessage() {

    }

    public ChatMessage(String message, int type, Date date) {
        super();
        this.message = message;
        this.type = type;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}