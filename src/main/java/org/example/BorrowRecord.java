package org.example;

import java.sql.Date;

public class BorrowRecord {
    private final int recordId;
    private final Book book;
    private final Member member;
    private final Date borrowDate;
    private final Date returnDate;
    private final double fine;

    public BorrowRecord(int recordId, Book book, Member member, Date borrowDate, Date returnDate, double fine) {
        this.recordId = recordId;
        this.book = book;
        this.member = member;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.fine = fine;
    }

    // Getters methods
    public int getRecordId() {
        return recordId;
    }

    public Book getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public double getFine() {
        return fine;
    }

}