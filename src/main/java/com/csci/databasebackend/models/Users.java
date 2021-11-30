package com.csci.databasebackend.models;

import lombok.Data;

import java.sql.Date;

@Data
public class Users {


    private int membershipNum;
    private String name;
    private Date birthDate;
    private String address;
    private String gender;

    public Users(int membershipNum, String name, Date birthDate, String address, String gender) {
        this.membershipNum = membershipNum;
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
        this.gender = gender;
    }

    public Users(String name, Date birthDate, String address, String gender) {
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
        this.gender = gender;
    }

    public Users() {
         name = "";
         birthDate = null;
         address = "";
         gender = "";
    }
}
