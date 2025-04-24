package tn.pi.artgallery.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tn.pi.artgallery.entities.User;
import tn.pi.artgallery.services.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Path("/admin/users")
@RolesAllowed("ADMIN")
@Produces(MediaType.APPLICATION_JSON)
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GET
    public Response getAllUsers(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        List<User> users = userService.getAllUsers(page, size);
        long totalUsers = userService.getTotalUsers();

        return Response.ok()
                .entity(Map.of(
                        "users", users,
                        "total", totalUsers,
                        "page", page,
                        "pageSize", size
                ))
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(User user) {
        try {
            // Set default role if not provided
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");
            }

            User createdUser = userService.createUser(user);
            createdUser.setPassword(null); // Never return password

            return Response.status(Response.Status.CREATED)
                    .entity(createdUser)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") Long id, User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            updatedUser.setPassword(null);

            return Response.ok()
                    .entity(updatedUser)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            userService.deleteUser(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/status")
    public Response toggleUserStatus(@PathParam("id") Long id) {
        try {
            userService.toggleUserStatus(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}