package com.example.cheatchat.cheatchatapps;

public class Posts {
    public String uid, time, profileimage, postimage, fullname, description, date;

    public Posts(){

    }

    public Posts(String uid, String time, String profileimage, String postimage, String fullname, String description, String date) {
        this.uid = uid;
        this.time = time;
        this.profileimage = profileimage;
        this.postimage = postimage;
        this.fullname = fullname;
        this.description = description;
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
