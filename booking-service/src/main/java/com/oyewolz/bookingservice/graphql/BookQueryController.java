package com.oyewolz.bookingservice.graphql;

import com.oyewolz.bookingservice.domain.Author;
import com.oyewolz.bookingservice.domain.Book;
import com.oyewolz.bookingservice.domain.BookCatalog;
import org.springframework.graphql.data.federation.EntityMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class BookQueryController {

    private final BookCatalog bookCatalog;

    public BookQueryController(BookCatalog bookCatalog) {
        this.bookCatalog = bookCatalog;
    }

    @QueryMapping
    public List<Book> books() {
        return bookCatalog.findAll();
    }

    @QueryMapping
    public Book bookById(@Argument String id) {
        return bookCatalog.findById(id).orElse(null);
    }

    @SchemaMapping(typeName = "Book", field = "author")
    public Author author(Book book) {
        return new Author(book.authorId());
    }

    @EntityMapping("Author")
    public Author authorByRepresentation(@Argument String id) {
        return new Author(id);
    }

    @SchemaMapping(typeName = "Author", field = "books")
    public List<Book> books(Author author) {
        return bookCatalog.findByAuthorId(author.id());
    }

    @SchemaMapping(typeName = "Author", field = "bookCount")
    public int bookCount(Author author) {
        return bookCatalog.countByAuthorId(author.id());
    }
}
