# Apollo Federation Sample with Spring Boot

This repo now demonstrates the federation scenario you asked for: query a `Book`, ask for its `author`, and let Apollo Router handle the cross-subgraph fetch.

## Services

- `author-service` owns the `Author` entity and exposes:
  - `authors`
  - `authorById(id: ID!)`
- `book-service` owns the `Book` entity and exposes:
  - `books`
  - `bookById(id: ID!)`

The federation link is:

- `Book.author` returns an `Author` reference from `book-service`
- `book-service` also contributes `Author.books` and `Author.bookCount`

That gives you both directions:

- from `Book` to `Author`
- from `Author` to the books written by that author

## How Apollo Routing Works Here

1. A client sends one query to the Apollo Router on port `4000`.
2. The router composes the subgraph schemas into one supergraph.
3. If a query starts with `books`, the router calls `book-service` first.
4. If the selection set also asks for `author { ... }`, the router uses `Author @key(fields: "id")` to call `author-service`.
5. The router merges both subgraph responses into one GraphQL result.

## Local Run

Start everything with Docker Compose:

```bash
docker compose up --build
```

Endpoints:

- Router: `http://localhost:4000`
- Author subgraph: `http://localhost:9081/graphql`
- Book subgraph: `http://localhost:9082/graphql`

If you want to refresh the composed schema snapshot manually:

```bash
APOLLO_ELV2_LICENSE=accept npx -y @apollo/rover supergraph compose --config federation/supergraph-config.yaml > federation/supergraph.graphql
```

## Example Queries Through The Router

This is the main federation scenario:

```graphql
query BookWithAuthor {
  books {
    id
    title
    genre
    publishedYear
    author {
      id
      name
      country
      era
    }
  }
}
```

This goes the other way and asks for book fields from the `Author` entity:

```graphql
query AuthorWithBooks {
  authors {
    id
    name
    bookCount
    books {
      id
      title
      isbn
    }
  }
}
```

## Learning Notes

- A subgraph is a normal GraphQL service plus federation metadata.
- Apollo Router does not route because of Java package names. It routes because the composed schema tells it which subgraph owns which fields.
- `Author @key(fields: "id")` is the contract that lets the router move from `book-service` to `author-service`.
- In Spring GraphQL, `@EntityMapping` resolves federated references like `{ "__typename": "Author", "id": "a1" }`.
- `Book.author` works because the book subgraph returns an `Author` reference with the key field the router needs.

## Files To Focus On

- `user-service/src/main/resources/graphql/schema.graphqls`
- `booking-service/src/main/resources/graphql/schema.graphqls`
- `user-service/src/main/java/com/oyewolz/userservice/graphql/AuthorQueryController.java`
- `booking-service/src/main/java/com/oyewolz/bookingservice/graphql/BookQueryController.java`
- `federation/supergraph-config.yaml`
- `federation/supergraph.graphql`
- `docker-compose.yml`
