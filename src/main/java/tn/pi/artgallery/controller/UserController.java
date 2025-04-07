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

            User loggedInUser = foundUser.get();

            // ✅ Retourner full_name, email et rôle dans la réponse
            return Response.ok().entity("{\"full_name\":\"" + loggedInUser.getFullName() +
                    "\", \"email\":\"" + loggedInUser.getEmail() +
                    "\", \"role\":\"" + loggedInUser.getRole() + "\"}").build();
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

}
