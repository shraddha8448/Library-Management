package org.example;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.io.*;

public class LibraryManagement {
    static  LibraryJDBC library;
    static BufferedReader br;

    void addBook()throws IOException,SQLException {
        System.out.println("Enter the book title");
        String bookTitle = br.readLine();
        System.out.println("Enter the author Name");
        String authorName = br.readLine();
        System.out.println("Enter the book number");
        String isbn = br.readLine();
        Book bookObj = new Book(isbn,bookTitle,authorName,true);
        //if(bookObj.getISBN().length() ==13){
            library.addBook(bookObj);
       // }
    }

    void removeBook()throws IOException,SQLException{
        System.out.println("Enter the book ISBN");
        String isbn = br.readLine();
        library.removeBook(isbn);
    }

    void searchBook()throws IOException,SQLException{
        System.out.println("Enter the book ISBN");
        String isbn = br.readLine();
        Book bookObj = library.searchBook(isbn);
        if(bookObj != null){
            System.out.println(bookObj.getTitle()+" is book is available");
        }else{
            System.out.println(isbn +" not available this book");
        }
    }

    void registerMember()throws IOException,SQLException{
        System.out.println("Enter the name for member registration");
        String name = br.readLine();
        Member member1 = new Member(0, name, new Date(System.currentTimeMillis()));
        library.registerMember(member1);
    }

    void removeMember()throws IOException,SQLException{
        System.out.println("Enter the member Id");
        int memberId = Integer.parseInt(br.readLine());
        library.removeMember(memberId);
    }

    void memberDetails()throws IOException,SQLException{
        System.out.println("Enter the member Id");
        int memberId = Integer.parseInt(br.readLine());
        Member obj = library.getMember(memberId);
        System.out.println("member ID: "+obj.getMemberId());
        System.out.println("member Name: "+obj.getName());
        System.out.println("membership date: "+obj.getMembershipDate());
        System.out.println("Borrowing Books: "+obj.getBorrowedBooks());
    }

    void borrowNewBook()throws IOException,SQLException{
        System.out.println("Enter the member Id");
        int memberId = Integer.parseInt(br.readLine());
        Member member = library.getMember(memberId);

        System.out.println("Enter the book Number (ISBN)");
        String isbn = br.readLine();
        Book book = library.searchBook(isbn);

        library.borrowBook(member,book);

    }

    void returnBook()throws IOException,SQLException{
        System.out.println("Enter the member Id");
        int memberId = Integer.parseInt(br.readLine());
        Member member = library.getMember(memberId);

        System.out.println("Enter the book Number (ISBN)");
        String isbn = br.readLine();
        Book book = library.searchBook(isbn);

        library.returnBook(member,book);

    }

//    void bookBorrowDate()throws IOException{
//
//        System.out.println("Enter the member Id");
//        int memberId = Integer.parseInt(br.readLine());
//
//        System.out.println("Enter the book number(isbn)");
//        String isbn = br.readLine();
//
//        Date borrowDate = library.getBorrowDate(memberId,isbn);
//        System.out.println("The borrowing date of "+ isbn + " book is: "+borrowDate);
//    }

    void borrowingHistory()throws IOException,SQLException{
        System.out.println("Enter the member Id");
        int memberId = Integer.parseInt(br.readLine());

        Member member = library.getMember(memberId);

        List<BorrowRecord> records = library.getBorrowingHistory(member);

        System.out.println("The borrowing books of "+member.getName() + " are:");
        for(BorrowRecord obj: records){
            System.out.println("Member Name:"+obj.getMember().getName()+" Book:"+obj.getBook().getTitle()+" Borrowing Date:"+obj.getBorrowDate()+" return Date:"+obj.getReturnDate()+" Fine:"+obj.getFine());
        }

    }


    public static void main(String[] args) {
        LibraryManagement LMObj = new LibraryManagement();
         br = new BufferedReader(new InputStreamReader(System.in));

        try {
            library = new LibraryJDBC();

            library.bookTable();
            library.membersTable();
            library.borrowRecordsTable();


//
//            // Get borrowing history
//            List<BorrowRecord> history = library.getBorrowingHistory(member1);
//            for (BorrowRecord record : history) {
//                System.out.println("Book: " + record.getBook().getTitle() +
//                        ", Borrowed on: " + record.getBorrowDate() +
//                        ", Returned on: " + record.getReturnDate() +
//                        ", Fine: $" + record.getFine());
//            }

            char ch = 'y';
            do{
                System.out.println("1.Add Book");
                System.out.println("2.Remove Book");
                System.out.println("3.search Book");
                System.out.println("4.Register new Member");
                System.out.println("5.Remove member");
                System.out.println("6.View member details");
                System.out.println("7.borrow a new Book");
                System.out.println("8.View Borrowing History");
                System.out.println("9.Return the book");

                System.out.println("Enter the choice");
                int choice = Integer.parseInt(br.readLine());

                switch (choice){
                    case 1: {
                                LMObj.addBook();
                            }
                            break;

                    case 2: {
                                 LMObj.removeBook();
                            }
                            break;
                    case 3: {
                                LMObj.searchBook();
                            }
                            break;
                    case 4: {
                                LMObj.registerMember();
                            }
                            break;

                    case 5: {
                                LMObj.removeMember();
                            }
                            break;

                    case 6 : {
                                LMObj.memberDetails();
                            }
                            break;

                    case 7: {
                                LMObj.borrowNewBook();
                            }
                            break;

                    case 8: {
                                LMObj.borrowingHistory();
                            }
                            break;

                    case 9:{
                                LMObj.returnBook();
                            }
                            break;

                    default:
                            System.out.println("Invalide input");
                }

                System.out.println("Do you want to continue ?");
                ch = br.readLine().charAt(0);

            }while (ch=='y' || ch=='Y');


        } catch (Exception e) {
            e.printStackTrace();
       }

    }
}
