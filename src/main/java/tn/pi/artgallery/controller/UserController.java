package tn.pi.artgallery.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tn.pi.artgallery.entities.User;
import tn.pi.artgallery.services.UserService;
import java.util.Optional;

@Component
@Path("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(User user) {
        userService.register(user);
        return Response.ok().entity("{\"message\":\"User registered successfully\"}").build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(User user, @Context HttpServletRequest request) {
        Optional<User> foundUser = userService.login(user.getEmail(), user.getPassword());

        if (foundUser.isPresent()) {
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", foundUser.get().getId());
            session.setAttribute("role", foundUser.get().getRole());

            return Response.ok().entity("{\"role\":\"" + foundUser.get().getRole() + "\"}").build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Invalid credentials\"}").build();
    }

    @POST
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Response.ok().entity("{\"message\":\"Logged out successfully\"}").build();
    }

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\":\"Not authenticated\"}").build();
        }

        Long userId = (Long) session.getAttribute("userId");
        Optional<User> user = userService.findById(userId);

        return user.map(value -> Response.ok(value).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"User not found\"}").build());
    }
}
