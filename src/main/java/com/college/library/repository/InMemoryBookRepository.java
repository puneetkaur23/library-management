package com.college.library.repository;

import com.college.library.model.Book;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link BookRepository}.
 * <p>
 * Uses a {@link HashMap} for O(1) ISBN lookups. Suitable for a console
 * application that does not need persistent storage.
 * </p>
 *
 * @author Puneet Kaur
 * @version 1.0.0
 */
public class InMemoryBookRepository implements BookRepository {

    /** Internal storage: ISBN -> Book mapping. */
    private final Map<String, Book> books = new HashMap<>();

    @Override
    public void save(Book book) {
        books.put(book.getIsbn(), book);
    }

    @Override
    public boolean deleteByIsbn(String isbn) {
        return books.remove(isbn) != null;
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }

    @Override
    public List<Book> findByTitleContaining(String title) {
        // Case-insensitive partial match on the title field
        String lowerTitle = title.toLowerCase();
        return books.values().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerTitle))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByAuthorContaining(String author) {
        // Case-insensitive partial match on the author field
        String lowerAuthor = author.toLowerCase();
        return books.values().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(lowerAuthor))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }
}

