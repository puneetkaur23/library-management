package com.college.library.repository;

import com.college.library.model.Book;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Book persistence operations.
 * <p>
 * Defines the contract for storing and retrieving books.
 * Following the Repository pattern (part of SOLID design) to decouple
 * business logic from data access.
 * </p>
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public interface BookRepository {

    /**
     * Saves a book to the repository.
     * @param book the book to save
     */
    void save(Book book);

    /**
     * Removes a book by its ISBN.
     * @param isbn the ISBN of the book to remove
     * @return {@code true} if the book was found and removed
     */
    boolean deleteByIsbn(String isbn);

    /**
     * Finds a book by its ISBN.
     * @param isbn the ISBN to search for
     * @return an Optional containing the book, or empty if not found
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Finds all books whose title contains the given keyword (case-insensitive).
     * @param title the title keyword to search for
     * @return a list of matching books
     */
    List<Book> findByTitleContaining(String title);

    /**
     * Finds all books whose author contains the given keyword (case-insensitive).
     * @param author the author keyword to search for
     * @return a list of matching books
     */
    List<Book> findByAuthorContaining(String author);

    /**
     * Returns all books in the repository.
     * @return a list of all books
     */
    List<Book> findAll();
}

