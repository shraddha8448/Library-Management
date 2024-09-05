package org.example;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private int memberId;
    private final String name;
    private final java.sql.Date membershipDate;
    private final List<Book> borrowedBooks = new ArrayList<>();

    public Member(int memberId, String name, java.sql.Date membershipDate) {
        this.memberId = memberId;
        this.name = name;
        this.membershipDate = membershipDate;
    }

    // Getters and Setters
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public java.sql.Date getMembershipDate() {
        return membershipDate;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }
}

