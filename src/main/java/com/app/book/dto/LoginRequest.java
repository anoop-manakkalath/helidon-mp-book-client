package com.app.book.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.json.bind.annotation.JsonbProperty;

@Schema(name = "LoginRequest", description = "Credentials for authentication")
public record LoginRequest(
    @Schema(required = true, example = "admin")
    @JsonbProperty("username")
    String username,

    @Schema(required = true, example = "pass123")
    @JsonbProperty("password")
    String password
) {}

