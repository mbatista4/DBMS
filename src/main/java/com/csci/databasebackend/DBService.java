package com.csci.databasebackend;

import com.csci.databasebackend.models.CheckoutBook;
import com.csci.databasebackend.models.UserCheckedOut;
import com.csci.databasebackend.models.Users;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // 1 select
    public List<Users> getAllUsers() {

        String sqlSelect = "Select * from users";

        try(Connection conn = DBConnection.createDBConnection()){
            stmt = conn.createStatement();
            results = stmt.executeQuery(sqlSelect);

            List<Users> studentsList = new ArrayList<>();

            while (results.next()) {

                Users stdObject = new Users();
                stdObject.setMembershipNum(Integer.valueOf(results.getString("MEMBERSHIP_NUM")));
                stdObject.setName(results.getString("name"));
                stdObject.setAddress(results.getString("Address"));
                stdObject.setGender(results.getString("GENDER"));
                stdObject.setBirthDate(results.getDate("BIRTHDATE"));

                studentsList.add(stdObject);
            }

            return studentsList;
        } catch (SQLException e ){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // 1 select
    public Mono<?> getSingleUser(String id) {
        String sqlSelect = "Select * from users where MEMBERSHIP_NUM =" + id;

        Users userFound = new Users();

        try(Connection conn = DBConnection.createDBConnection()){
            stmt = conn.createStatement();
            results = stmt.executeQuery(sqlSelect);

            if (!results.next()){
                return Mono.just(new ResponseEntity<String>("User not found with membership_num=" + id, HttpStatus.NOT_FOUND));
            }

            userFound.setMembershipNum(Integer.valueOf(results.getString("MEMBERSHIP_NUM")));
            userFound.setName(results.getString("name"));
            userFound.setAddress(results.getString("Address"));
            userFound.setGender(results.getString("GENDER"));
            userFound.setBirthDate(results.getDate("BIRTHDATE"));

            ObjectMapper mapper = new ObjectMapper();
            System.out.println(userFound);
            System.out.println(results);
            return Mono.just(new ResponseEntity<Users>(userFound, HttpStatus.OK));
        } catch (SQLException e ){
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        }
        return Mono.just(new ResponseEntity<String>("please enter number for the membership", HttpStatus.NOT_FOUND));
    }

    // 1 update
    public Mono<?> changeUsersAddress(String address, int membershipId) {
        String sqlSelect = String.format("UPDATE users set address = '%s' where MEMBERSHIP_NUM = %d",address,membershipId);

        try(Connection conn = DBConnection.createDBConnection()){
            stmt = conn.createStatement();
            int a = stmt.executeUpdate(sqlSelect);

            if(a > 0){
                return Mono.just(new ResponseEntity<String>("Address updated successfully",HttpStatus.OK));
            }

            return Mono.just(new ResponseEntity<String>("Member with membership_num=" + membershipId + " does not exist",HttpStatus.NOT_FOUND));

        } catch (SQLException e ){
            e.printStackTrace();
        }

        return Mono.just(new ResponseEntity<String>("Error connecting to db",HttpStatus.SERVICE_UNAVAILABLE));
    }

    // 1 select
    public Mono<?> getMemberWithCheckoutBooks(int membershipId) {

        UserCheckedOut userCheckedOut = new UserCheckedOut();

        String sqlSelectQuery = "select checks_out.membership_num, name, title from checks_out" +
                " join books on checks_out.isbn = books.isbn" +
                " join users on checks_out.membership_num = users.membership_num  where checks_out.membership_num = " + membershipId + ";";

        try(Connection conn = DBConnection.createDBConnection()) {
            stmt = conn.createStatement();
            results = stmt.executeQuery(sqlSelectQuery);

            if (!results.next()){
                return Mono.just(new ResponseEntity<>(String.format("Member with id=%d does not have a book checkout",membershipId), HttpStatus.NOT_FOUND));
            }

            userCheckedOut.setMembershipId(membershipId);
            userCheckedOut.setName(results.getString("name"));
            userCheckedOut.setBookName(results.getString("title"));

            return Mono.just(new ResponseEntity<UserCheckedOut>(userCheckedOut,HttpStatus.OK));

        } catch (SQLException e ){
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        }
        return Mono.just(new ResponseEntity<String>("Error occurred", HttpStatus.SERVICE_UNAVAILABLE));
    }

    // 1 select
    public Mono<?> getAllMemberWithCheckoutBooks() {
        String sqlSelectQuery = "select checks_out.membership_num, users.name, title from checks_out" +
                                " join books on checks_out.isbn = books.isbn" +
                                " join users on checks_out.membership_num = users.membership_num;";

        System.out.println(sqlSelectQuery);
        try(Connection conn = DBConnection.createDBConnection()){
            stmt = conn.createStatement();
            results = stmt.executeQuery(sqlSelectQuery);

            List<UserCheckedOut> checkedOutList = new ArrayList<>();

            if(!results.next()) {
                return Mono.just(new ResponseEntity<>("No books are checked out",HttpStatus.OK));
            }

            do {
                UserCheckedOut temp = new UserCheckedOut();
                temp.setMembershipId(results.getInt("membership_num"));
                temp.setName(results.getString("name"));
                temp.setBookName(results.getString("title"));

                checkedOutList.add(temp);
            } while(results.next());

            return Mono.just(new ResponseEntity<>(checkedOutList, HttpStatus.OK));

        } catch (SQLException e ){
            e.printStackTrace();
        }
        return Mono.just(new ResponseEntity<>("Error occurred in the server",HttpStatus.SERVICE_UNAVAILABLE));
    }

    /*
     * 1 select
     * 1 insert
     * 1 update
     */
    public Mono<?> checkoutBook(CheckoutBook newCheckout) {

        String sqlSelect = String.format("Select quantity from books where isbn = '%s'",newCheckout.getIsbn());
        String sqlInsert = String.format("Insert into checks_out(`membership_num`,`isbn`) values(%d,'%s');",
                newCheckout.getMembership_num(),newCheckout.getIsbn());


        try(Connection conn = DBConnection.createDBConnection()){
            stmt = conn.createStatement();
            results = stmt.executeQuery(sqlSelect);

            if(!results.next()){
                return Mono.just(new ResponseEntity<>(String.format("ISBN: %s IS NOT FOUND",newCheckout.getIsbn()),HttpStatus.NOT_FOUND));
            }
            int quantity = results.getInt("quantity") - 1;

            System.out.println(quantity);

            if(quantity < 0) {
                return Mono.just(new ResponseEntity<>(String.format("All books with ISBN:%s are checked out",newCheckout.getIsbn()),HttpStatus.CONFLICT));
            }

            int stmtResult = stmt.executeUpdate(sqlInsert);

            if (stmtResult  !=1 ) {
                return Mono.just(new ResponseEntity<>(String.format("Membership num: %d does not exist or has already checked out a book",newCheckout.getMembership_num()),HttpStatus.CONFLICT));
            }

            String sqlUpdate = String.format("UPDATE books set quantity = %d where isbn = '%s'",quantity,newCheckout.getIsbn());


            stmtResult = stmt.executeUpdate(sqlUpdate);

            if (stmtResult  !=1 ) {
                return Mono.just(new ResponseEntity<>(String.format("error updating book count of book with isbn= %s",newCheckout.getIsbn()),HttpStatus.CONFLICT));
            }

            return Mono.just(new ResponseEntity<String>("checkout successful",HttpStatus.OK));

        } catch (SQLException e ){
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>(e.getLocalizedMessage(),HttpStatus.SERVICE_UNAVAILABLE));
        }

    }

    /*
    * 1 delete
    * 1 retrieve
    * 1 update
     */
    public Mono<?> checkinBook(CheckoutBook newCheckin) {
        String sqlSelect = String.format("Select quantity from books where isbn = '%s'",newCheckin.getIsbn());
        String sqlDelete = String.format("delete from checks_out where membership_num=%d",newCheckin.getMembership_num());


        try(Connection conn = DBConnection.createDBConnection()){
            stmt = conn.createStatement();
            int stmtResult = stmt.executeUpdate(sqlDelete);
            if(stmtResult != 1){
                return Mono.just(new ResponseEntity<>(String.format("ISBN: %s could not be returned try again later",newCheckin.getIsbn()),HttpStatus.CONFLICT));
            }

            results = stmt.executeQuery(sqlSelect);
            if(!results.next()){
                return Mono.just(new ResponseEntity<>(String.format("ISBN: %s was not found",newCheckin.getIsbn()),HttpStatus.CONFLICT));
            }

            int quantity = results.getInt("quantity") + 1;

            String sqlUpdate = String.format("UPDATE books set quantity = %d where isbn = '%s'",quantity,newCheckin.getIsbn());

            stmt.executeUpdate(sqlUpdate);

            return Mono.just(new ResponseEntity<String>("checkin successful",HttpStatus.OK));

        } catch (SQLException e ){
            e.printStackTrace();
            return Mono.just(new ResponseEntity<>(e.getLocalizedMessage(),HttpStatus.SERVICE_UNAVAILABLE));
        }

    }
}
