package com.oyewolz.bookingservice.domain;

public record Book(
        String id,
        String title,
        String genre,
        String isbn,
        int publishedYear,
        String authorId
) {
}
