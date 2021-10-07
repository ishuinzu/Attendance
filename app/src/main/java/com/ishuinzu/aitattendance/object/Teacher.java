package com.ishuinzu.aitattendance.object;

public class Teacher {
    private long creation;
    private String department;
    private String email;
    private String id;
    private String img_link;
    private Boolean is_verified;
    private String name;
    private String password;

    public Teacher() {
    }

    public Teacher(long creation, String department, String email, String id, String img_link, Boolean is_verified, String name, String password) {
        this.creation = creation;
        this.department = department;
        this.email = email;
        this.id = id;
        this.img_link = img_link;
        this.is_verified = is_verified;
        this.name = name;
        this.password = password;
    }

    public long getCreation() {
        return creation;
    }

    public void setCreation(long creation) {
        this.creation = creation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public Boolean getIs_verified() {
        return is_verified;
    }

    public void setIs_verified(Boolean is_verified) {
        this.is_verified = is_verified;
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