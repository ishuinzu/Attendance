package com.ishuinzu.aitattendance.object;

public class Admin {
    private long creation;
    private String email;
    private String id;
    private String img_link;
    private String name;
    private String password;

    public Admin() {
    }

    public Admin(long creation, String email, String id, String img_link, String name, String password) {
        this.creation = creation;
        this.email = email;
        this.id = id;
        this.img_link = img_link;
        this.name = name;
        this.password = password;
    }

    public long getCreation() {
        return creation;
    }

    public void setCreation(long creation) {
        this.creation = creation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg_link() {
        return img_link;
    }

    public void setImg_link(String img_link) {
        this.img_link = img_link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}