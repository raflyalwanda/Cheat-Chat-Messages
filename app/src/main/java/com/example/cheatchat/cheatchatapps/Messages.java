package com.example.cheatchat.cheatchatapps;

public class Messages {
    private String from, message, type,seen;
    public Messages(){

    }

    public Messages(String from, String message, String type, String seen ) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.seen = seen;

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }
}

