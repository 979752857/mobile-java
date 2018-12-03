package com.tendy.model;

public class SendIM {
    private String phone;
    private String tag;
    private String key;

    public SendIM(String phone, String tag, String key) {
        this.phone = phone;
        this.tag = tag;
        this.key = key;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}