package com.app.book.resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.app.book.dto.LoginRequest;
import com.app.book.dto.User;
import com.app.book.service.JwtService;

import io.helidon.security.abac.role.RoleValidator.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
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
    @PermitAll
    public Response login(LoginRequest request) throws Exception {
        var user = userDb.get(request.username());
        if (Optional.ofNullable(user).map(User::password).filter(pass -> pass.equals(request.password()))
                .isPresent()) {
            var token = jwtService.generateToken(user);
            return Response.ok(Map.of("access_token", token)).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
