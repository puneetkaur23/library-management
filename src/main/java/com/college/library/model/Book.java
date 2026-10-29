package com.college.library.model;

import org.apache.commons.lang3.StringUtils;
import java.util.Objects;

/**
 * Represents a book in the library system.
 * <p>
 * Each book has a unique ISBN, a title, an author, and an availability status
 * that tracks whether the book is currently available for borrowing.
 * </p>
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public class Book {

    /** The unique ISBN (International Standard Book Number) identifier. */
    private final String isbn;

    /** The title of the book. */
    private String title;

    /** The author of the book. */
    private String author;

    /** Whether the book is currently available for borrowing. */
    private boolean available;

    /**
     * Constructs a new Book with the given details.
     * The book is marked as available by default.
     *
     * @param isbn   the ISBN identifier (must not be blank)
     * @param title  the book title (must not be blank)
     * @param author the book author (must not be blank)
     * @throws IllegalArgumentException if any parameter is blank
     */
    public Book(String isbn, String title, String author) {
        // Validate that none of the required fields are blank
        if (StringUtils.isBlank(isbn)) {
            throw new IllegalArgumentException("ISBN must not be blank");
        }
        if (StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("Title must not be blank");
        }
        if (StringUtils.isBlank(author)) {
            throw new IllegalArgumentException("Author must not be blank");
        }
        this.isbn = isbn.trim();
        this.title = title.trim();
        this.author = author.trim();
        this.available = true; // New books are available by default
    }

    /** @return the ISBN of this book */
    public String getIsbn() {
        return isbn;
    }

    /** @return the title of this book */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this book.
     * @param title the new title (must not be blank)
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return the author of this book */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of this book.
     * @param author the new author (must not be blank)
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /** @return {@code true} if the book is available for borrowing */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets the availability status of this book.
     * @param available {@code true} to mark as available, {@code false} otherwise
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Two books are equal if they share the same ISBN.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return String.format("Book{isbn='%s', title='%s', author='%s', available=%s}",
                isbn, title, author, available);
    }
}

