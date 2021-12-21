package com.csci.databasebackend.controllers;

import com.csci.databasebackend.DBService;
import com.csci.databasebackend.models.CheckoutBook;
import com.csci.databasebackend.models.Users;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Data
    private static class Address {
        String address;
    }

    @Autowired
    DBService dbService;

    /*
     * @Param returns a list of users
     * @
     */
    @GetMapping("/users")
    Mono<List<Users>> getAllUsers() {
        return Mono.just(dbService.getAllUsers());
    }

    @GetMapping("/get_single_user")
    Mono<?> getSingleUser(@RequestParam String membership_num) {
        return dbService.getSingleUser(membership_num);
    }

    @PutMapping("/change_address")
    Mono<?> updateAddress(@RequestParam(value = "membership_num") int membershipNum, @RequestBody(required = true) Address newAddress ) {
        return Mono.just(dbService.changeUsersAddress(newAddress.getAddress(),membershipNum));
    }

    @GetMapping("/get_book_checked_out_for_single_member")
    Mono<?> getSingleCheckout(@RequestParam int membership_num) {
        return dbService.getMemberWithCheckoutBooks(membership_num);
    }

    @GetMapping("/get_all_members_with_a_book_check_out")
    Mono<?> getAllCheckouts() {
        return dbService.getAllMemberWithCheckoutBooks();
    }

    @PostMapping("/checkout_book")
    Mono<?> checkoutBook(@RequestBody CheckoutBook newCheckout) { return dbService.checkoutBook(newCheckout);}

    @PostMapping("/checking_book")
    Mono<?> checkinBook(@RequestBody CheckoutBook oldCheckout) { return dbService.checkinBook(oldCheckout);}


}
