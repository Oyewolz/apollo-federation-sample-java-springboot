package com.oyewolz.userservice.domain;

public record Author(
        String id,
        String name,
        String country,
        String era
) {
}
