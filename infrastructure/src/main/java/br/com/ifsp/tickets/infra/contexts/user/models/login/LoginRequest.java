package br.com.ifsp.tickets.infra.contexts.user.models.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginRequest(

        @JsonProperty("login") String login,
        @JsonProperty("password") String password
) {
}
