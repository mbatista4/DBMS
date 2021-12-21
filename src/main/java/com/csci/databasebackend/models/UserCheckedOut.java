package com.csci.databasebackend.models;

import lombok.Data;

@Data
public class UserCheckedOut {

    int membershipId;
    String name;
    String bookName;
}
