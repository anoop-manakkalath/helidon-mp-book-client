package com.app.book.resource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.app.book.dto.LoginRequest;
import com.app.book.dto.User;
import com.app.book.service.JwtService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@ApplicationScoped
public class AuthResource {
	
    private final JwtService jwtService;

    // Mock Database
    private final Map<String, User> userDb = Map.of(
        "admin", new User("admin", "pass123", List.of("admin")),
        "guest", new User("guest", "pass456", List.of("user"))
    );
    
    @Inject
    public AuthResource(JwtService jwtService) {
    	this.jwtService = jwtService;
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest request) throws Exception {
        var user = userDb.get(request.username());
        if (Objects.nonNull(user) && user.password().equals(request.password())) {
            var token = jwtService.generateToken(user);
            return Response.ok(Map.of("access_token", token)).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
