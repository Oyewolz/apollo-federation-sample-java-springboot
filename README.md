# Apollo Federation Sample with Spring Boot

This repo contains two Spring Boot GraphQL subgraphs and an Apollo Router setup for learning how subgraph routing works.

## Services

- `user-service` owns the `User` entity and exposes:
  - `users`
  - `userById(id: ID!)`
- `booking-service` owns bookings and exposes:
  - `bookings`
  - `bookingById(id: ID!)`

The interesting part is the federation link:

- `Booking.user` returns a `User` reference from `booking-service` to `user-service`
- `booking-service` also contributes `User.bookings` and `User.activeBookingCount`

That means the Apollo Router can route in both directions:

- from `Booking` to `User`
- from `User` to booking-specific fields

## How Apollo Routing Works Here

1. A client sends one query to the Apollo Router on port `4000`.
2. The router reads the composed supergraph schema in `federation/supergraph.graphql`.
3. If the query needs user-owned fields, the router calls `user-service`.
4. If the query needs booking-owned fields, the router calls `booking-service`.
5. For shared entity resolution, the router uses the `User @key(fields: "id")` definition to move between subgraphs.

## Local Run

Start everything with Docker Compose:

```bash
docker compose up --build
```

The router container uses `rover dev` to compose the supergraph from `federation/supergraph-config.yaml` and run Apollo routing on port `4000`.

Endpoints:

- Router: `http://localhost:4000/graphql`
- User subgraph: `http://localhost:9081/graphql`
- Booking subgraph: `http://localhost:9082/graphql`

If you want to inspect the composed schema snapshot yourself, you can also regenerate the checked-in `federation/supergraph.graphql` file manually:

```bash
APOLLO_ELV2_LICENSE=accept npx -y @apollo/rover supergraph compose --config federation/supergraph-config.yaml > federation/supergraph.graphql
```

## Example Queries Through The Router

This query starts in `booking-service` and then routes to `user-service` for user fields:

```graphql
query BookingToUser {
  bookings {
    id
    code
    status
    user {
      id
      name
      email
      tier
    }
  }
}
```

This query starts in `user-service` and then routes to `booking-service` for booking fields:

```graphql
query UserToBooking {
  users {
    id
    name
    activeBookingCount
    bookings {
      id
      code
      status
      notes
    }
  }
}
```

## Learning Notes

- A subgraph is a GraphQL service that publishes a piece of the overall schema plus federation metadata.
- The Apollo Router does not infer routes from Java packages or controllers. It routes based on the composed supergraph schema.
- `@key(fields: "id")` tells the router that `User` can be identified across subgraphs by `id`.
- `@EntityMapping` methods are how Spring GraphQL resolves federated entities when the router sends representations like `{ "__typename": "User", "id": "u1" }`.
- `Booking.user` works because the booking subgraph returns a reference object that includes `id`. The router uses that key to fetch the rest of the user fields from the user subgraph.
- `User.bookings` works because `booking-service` also understands the `User` entity key and can contribute booking-specific fields for that entity.

## Files To Focus On

- `user-service/src/main/resources/graphql/schema.graphqls`
- `booking-service/src/main/resources/graphql/schema.graphqls`
- `user-service/src/main/java/com/oyewolz/userservice/graphql/UserQueryController.java`
- `booking-service/src/main/java/com/oyewolz/bookingservice/graphql/BookingQueryController.java`
- `federation/supergraph-config.yaml`
- `federation/supergraph.graphql`
- `docker-compose.yml`
