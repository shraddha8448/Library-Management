package org.example;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class LibraryJDBC {
    private final Connection connection;

    public LibraryJDBC() throws SQLException, ClassNotFoundException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        // Initialize JDBC connection
        String url = "jdbc:mysql://localhost:3306/library1";
        String user = "root";
        String password = "shraddha#19";
        connection = DriverManager.getConnection(url, user, password);
    }

    // creating Book Info table
    public void bookTable(){
        String sqlQuery = "CREATE TABLE IF NOT EXISTS books( isbn VARCHAR(13) PRIMARY KEY, title VARCHAR(50), author VARCHAR(50),isAvailable BOOLEAN)";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // creating members Info table
    public void membersTable(){
        String sqlQuery = "CREATE TABLE IF NOT EXISTS members(memberId INT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(50),membershipDate DATE)";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // creating table for borrow records
    public void borrowRecordsTable(){
        String sqlQuery = "CREATE TABLE IF NOT EXISTS borrow_records(recordId INT AUTO_INCREMENT PRIMARY KEY,isbn VARCHAR(13),memberId INT,borrowDate DATE,returnDate DATE,fine DECIMAL(5,2), FOREIGN KEY(ISBN) REFERENCES books(ISBN)ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY (memberId) REFERENCES members(memberId)ON DELETE CASCADE ON UPDATE CASCADE)";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // add a Book in database
    public void addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (ISBN, title, author, isAvailable) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getISBN());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setBoolean(4, book.isAvailable());
            stmt.executeUpdate();
        }
    }

    // remove book from Table
    public void removeBook(String ISBN) throws SQLException {
        String sql = "DELETE FROM books WHERE ISBN = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ISBN);
            stmt.executeUpdate();
        }
    }

    // Searching Book from the table
    public Book searchBook(String ISBN) throws SQLException {
        String sql = "SELECT * FROM books WHERE ISBN = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, ISBN);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Book(
                        rs.getString("ISBN"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getBoolean("isAvailable")
                );
            }
        }
        return null;
    }

    // Register new Member
    public void registerMember(Member member) throws SQLException {
        String sql = "INSERT INTO members (name, membershipDate) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, member.getName());
            stmt.setDate(2, member.getMembershipDate());
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                member.setMemberId(generatedKeys.getInt(1));
            }
        }
    }

    // Remove Member from the database
    public void removeMember(int memberId) throws SQLException {
        String sql = "DELETE FROM members WHERE memberId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
        }
    }

    // get member Info
    public Member getMember(int memberId) throws SQLException {
        String sql = "SELECT * FROM members WHERE memberId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Member(
                        rs.getInt("memberId"),
                        rs.getString("name"),
                        rs.getDate("membershipDate")
                );
            }
        }
        return null;
    }

    // Borrowing Books
    public void borrowBook(Member member, Book book) throws SQLException {
        if (!book.isAvailable()) {
            System.out.println("Book is not available.");
            return;
        }
        String sql = "INSERT INTO borrow_records (ISBN, memberId, borrowDate) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getISBN());
            stmt.setInt(2, member.getMemberId());
            stmt.setDate(3, new Date(System.currentTimeMillis()));
            stmt.executeUpdate();

            book.setAvailable(false);
            updateBookAvailability(book);
        }
    }

    // Return the book
    public void returnBook(Member member, Book book) throws SQLException {
        String sql = "UPDATE borrow_records SET returnDate = ?, fine = ? WHERE ISBN = ? AND memberId = ? AND returnDate IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            Date returnDate = new Date(System.currentTimeMillis());
            stmt.setDate(1, returnDate);
            stmt.setDouble(2, calculateFine(member, book, returnDate));
            stmt.setString(3, book.getISBN());
            stmt.setInt(4, member.getMemberId());
            stmt.executeUpdate();

            book.setAvailable(true);
            updateBookAvailability(book);
        }
    }

    public Date getBorrowDate(int memberId, String ISBN) {
        String sql = "SELECT borrowDate FROM borrow_records WHERE memberId = ? AND ISBN = ? AND returnDate IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setString(2, ISBN);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDate("borrowDate");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // No borrow record found or an error occurred
    }


    // Fine calculation
    private double calculateFine(Member member, Book book, Date returnDate) {

        final int MAX_BORROW_DAYS = 30; // Maximum allowed borrow days
        final double DAILY_FINE = 1.0; // Fine per day for overdue books

        // Fetch the borrow date from the database
        Date borrowDate = getBorrowDate(member.getMemberId(), book.getISBN());

        if (borrowDate == null) {
            // No borrow record found
            return 0.0;
        }

        // Calculate the difference in days between the return date and the borrow date
        long diffInMillis = returnDate.getTime() - borrowDate.getTime();
        long diffInDays = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

        // Calculate the fine if the return is overdue
        if (diffInDays > MAX_BORROW_DAYS) {
            long lateDays = diffInDays - MAX_BORROW_DAYS;
            return lateDays * DAILY_FINE;
        } else {
            return 0.0; // No fine if returned within the allowed period
        }


    }

    // For Book available or not
    private void updateBookAvailability(Book book) throws SQLException {
        String sql = "UPDATE books SET isAvailable = ? WHERE ISBN = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, book.isAvailable());
            stmt.setString(2, book.getISBN());
            stmt.executeUpdate();
        }
    }

    // Viewing Borrowing History
    public List<BorrowRecord> getBorrowingHistory(Member member) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE memberId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, member.getMemberId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Book book = searchBook(rs.getString("ISBN"));
                records.add(new BorrowRecord(
                        rs.getInt("recordId"),
                        book,
                        member,
                        rs.getDate("borrowDate"),
                        rs.getDate("returnDate"),
                        rs.getDouble("fine")
                ));
            }
        }
        return records;
    }
}

