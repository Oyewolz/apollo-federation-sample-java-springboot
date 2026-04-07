package com.oyewolz.bookingservice.domain;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookCatalog {

    private final Map<String, Book> books = List.of(
            new Book("b1", "Half of a Yellow Sun", "Historical Fiction", "9780007200283", 2006, "a1"),
            new Book("b2", "Americanah", "Literary Fiction", "9780307455925", 2013, "a1"),
            new Book("b3", "1984", "Dystopian Fiction", "9780451524935", 1949, "a2"),
            new Book("b4", "Beloved", "Historical Fiction", "9781400033416", 1987, "a3")
    ).stream().collect(Collectors.toUnmodifiableMap(Book::id, Function.identity()));

    public List<Book> findAll() {
        return books.values().stream().toList();
    }

    public Optional<Book> findById(String id) {
        return Optional.ofNullable(books.get(id));
    }

    public List<Book> findByAuthorId(String authorId) {
        return books.values().stream()
                .filter(book -> book.authorId().equals(authorId))
                .toList();
    }

    public int countByAuthorId(String authorId) {
        return (int) books.values().stream()
                .filter(book -> book.authorId().equals(authorId))
                .count();
    }
}
