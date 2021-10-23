package com.ishuinzu.aitattendance.object;

public class SMS {
    private String address;
    private String by_name;
    private String by_type;
    private Long creation;
    private String department;
    private String father_name;
    private String message_text;
    private String message_type;
    private String name;
    private String phone_number_01;
    private String phone_number_02;
    private String roll_number;
    private String section;

    public SMS() {
    }

    public SMS(String address, String by_name, String by_type, Long creation, String department, String father_name, String message_text, String message_type, String name, String phone_number_01, String phone_number_02, String roll_number, String section) {
        this.address = address;
        this.by_name = by_name;
        this.by_type = by_type;
        this.creation = creation;
        this.department = department;
        this.father_name = father_name;
        this.message_text = message_text;
        this.message_type = message_type;
        this.name = name;
        this.phone_number_01 = phone_number_01;
        this.phone_number_02 = phone_number_02;
        this.roll_number = roll_number;
        this.section = section;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBy_name() {
        return by_name;
    }

    public void setBy_name(String by_name) {
        this.by_name = by_name;
    }

    public String getBy_type() {
        return by_type;
    }

    public void setBy_type(String by_type) {
        this.by_type = by_type;
    }

    public Long getCreation() {
        return creation;
    }

    public void setCreation(Long creation) {
        this.creation = creation;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFather_name() {
        return father_name;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number_01() {
        return phone_number_01;
    }

    public void setPhone_number_01(String phone_number_01) {
        this.phone_number_01 = phone_number_01;
    }

    public String getPhone_number_02() {
        return phone_number_02;
    }

    public void setPhone_number_02(String phone_number_02) {
        this.phone_number_02 = phone_number_02;
    }

    public String getRoll_number() {
        return roll_number;
    }

    public void setRoll_number(String roll_number) {
        this.roll_number = roll_number;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
}