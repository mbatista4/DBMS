package com.csci.databasebackend;

import com.csci.databasebackend.models.Users;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
public class DBService {

    private static Statement stmt;
    private static ResultSet results;

    public List<Users> getAllUsers() {

        String sqlSelect = "Select * from users";

        try(Connection conn = DBConnection.createDBConnection()){
            stmt = conn.createStatement();
            results = stmt.executeQuery(sqlSelect);

            List<Users> studentsList = new ArrayList<Users>();

            while (results.next()) {

                Users stdObject = new Users();

//                private String name;
//                private Date birthDate;
//                private String address;
//                private String gender;

                stdObject.setMembershipNum(Integer.valueOf(results.getString("MEMBERSHIP_NUM")));
                stdObject.setName(results.getString("name"));
                stdObject.setAddress(results.getString("Address"));
                stdObject.setGender(results.getString("GENDER"));
                stdObject.setBirthDate(results.getDate("BIRTHDATE"));

                studentsList.add(stdObject);
            }

            ObjectMapper mapper = new ObjectMapper();
            String JSONOutput = mapper.writeValueAsString(studentsList);
            System.out.println(JSONOutput);
            return studentsList;
        } catch (SQLException e ){
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
