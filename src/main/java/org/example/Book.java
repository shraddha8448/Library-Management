package org.example;

public class Book {
    private final String ISBN;
    private final String title;
    private final String author;
    private boolean isAvailable;

    public Book(String ISBN, String title, String author, boolean isAvailable) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.isAvailable = isAvailable;
    }

    // Getters methods
    public String getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
