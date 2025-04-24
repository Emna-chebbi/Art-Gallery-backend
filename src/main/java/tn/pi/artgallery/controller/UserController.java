package tn.pi.artgallery.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ssl.SslProperties;
import org.springframework.stereotype.Component;
import tn.pi.artgallery.entities.User;
import tn.pi.artgallery.services.UserService;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
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
        try {
            User registeredUser = userService.register(user);
            return Response.ok()
                    .entity(registeredUser)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(User user, @Context HttpServletRequest request) {
        try {
            Optional<User> foundUser = userService.login(user.getEmail(), user.getPassword());
            if (foundUser.isPresent()) {
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", foundUser.get().getId());
                session.setAttribute("role", foundUser.get().getRole());

                // Return user without password
                User responseUser = foundUser.get();
                responseUser.setPassword(null); // Remove password from response

                return Response.ok()
                        .entity(responseUser)
                        .build();
            }
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Invalid credentials\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Login failed: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return Response.ok()
                    .entity(user.get())
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"User not found\"}")
                .build();
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpServletRequest request) {
        try {
            // Get the current session
            HttpSession session = request.getSession(false);

            if (session != null) {
                // Invalidate the session (clears all session attributes)
                session.invalidate();
            }

            return Response.ok()
                    .entity("{\"message\":\"Logged out successfully\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Logout failed: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    @PUT
    @Path("/user/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(
            @PathParam("id") Long id,
            @FormDataParam("fullName") String fullName,
            @FormDataParam("email") String email,
            @FormDataParam("phone") String phone,
            @FormDataParam("address") String address,
            @FormDataParam("image") InputStream imageInputStream,
            @FormDataParam("image") FormDataContentDisposition fileDetail
    ) {
        try {
            User userDetails = new User();
            userDetails.setFullName(fullName);
            userDetails.setEmail(email);
            userDetails.setPhone(phone);
            userDetails.setAddress(address);

            // Handle image upload
            if (imageInputStream != null && fileDetail != null) {
                // Create uploads directory if it doesn't exist
                String uploadDir = "uploads/";
                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Generate unique filename
                String fileExtension = fileDetail.getFileName()
                        .substring(fileDetail.getFileName().lastIndexOf("."));
                String fileName = "user_" + id + "_" + System.currentTimeMillis() + fileExtension;
                String filePath = uploadDir + fileName;

                // Save file
                Files.copy(imageInputStream, Paths.get(filePath),
                        StandardCopyOption.REPLACE_EXISTING);

                // Set the full URL path that points to your backend
                String imageUrl = "http://localhost:8080/" + filePath;
                userDetails.setImageUrl(imageUrl);
            }

            User updatedUser = userService.updateUser(id, userDetails);
            updatedUser.setPassword(null); // Don't return password

            return Response.ok()
                    .entity(updatedUser)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
    @GET
    @Path("/count")
    public Response getUsersCount() {
        long count = userService.getUsersCount();
        return Response.ok().entity(count).build();
    }

}