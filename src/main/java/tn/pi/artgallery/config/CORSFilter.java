package tn.pi.artgallery.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {
        // Change to your Angular app's origin (or use a list of allowed origins)
        responseContext.getHeaders().add(
                "Access-Control-Allow-Origin", "http://localhost:4200");

        // Must be true when using withCredentials
        responseContext.getHeaders().add(
                "Access-Control-Allow-Credentials", "true");

        // Add all needed headers
        responseContext.getHeaders().add(
                "Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization, x-requested-with");

        responseContext.getHeaders().add(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");

        // Handle preflight requests
        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            responseContext.getHeaders().add(
                    "Access-Control-Max-Age", "86400"); // 24 hours
        }
    }
}