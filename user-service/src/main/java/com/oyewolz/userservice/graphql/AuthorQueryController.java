package com.oyewolz.userservice.graphql;

import com.oyewolz.userservice.domain.Author;
import com.oyewolz.userservice.domain.AuthorCatalog;
import org.springframework.graphql.data.federation.EntityMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AuthorQueryController {

    private final AuthorCatalog authorCatalog;

    public AuthorQueryController(AuthorCatalog authorCatalog) {
        this.authorCatalog = authorCatalog;
    }

    @QueryMapping
    public List<Author> authors() {
        return authorCatalog.findAll();
    }

    @QueryMapping
    public Author authorById(@Argument String id) {
        return authorCatalog.findById(id).orElse(null);
    }

    @EntityMapping("Author")
    public Author author(@Argument String id) {
        return authorCatalog.findById(id).orElse(null);
    }
}
