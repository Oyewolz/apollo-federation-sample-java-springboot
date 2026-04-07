package com.oyewolz.userservice.domain;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AuthorCatalog {

    private final Map<String, Author> authors = List.of(
            new Author("a1", "Chimamanda Ngozi Adichie", "Nigeria", "Contemporary"),
            new Author("a2", "George Orwell", "United Kingdom", "Modernist"),
            new Author("a3", "Toni Morrison", "United States", "Contemporary")
    ).stream().collect(Collectors.toUnmodifiableMap(Author::id, Function.identity()));

    public List<Author> findAll() {
        return authors.values().stream().toList();
    }

    public Optional<Author> findById(String id) {
        return Optional.ofNullable(authors.get(id));
    }
}
