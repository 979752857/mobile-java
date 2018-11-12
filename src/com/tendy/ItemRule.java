package com.tendy;

public class ItemRule {
    private String pattern;
    private String remark;
    private String tag;
    private String phone;
    private String keyword;

    public ItemRule(String pattern, String remark, String tag){
        this.pattern = pattern;
        this.remark = remark;
        this.tag = tag;
    }

    public ItemRule(String pattern, String remark, String tag, String phone, String keyword){
        this.pattern = pattern;
        this.remark = remark;
        this.tag = tag;
        this.phone = phone;
        this.keyword = keyword;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}