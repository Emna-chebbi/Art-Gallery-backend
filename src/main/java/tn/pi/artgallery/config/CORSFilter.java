package tn.pi.artgallery.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

// Temporary testing modification
@Provider
public class CORSFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {
        responseContext.getHeaders().add(
                "Access-Control-Allow-Origin", "*"); // Changed to allow all
        responseContext.getHeaders().add(
                "Access-Control-Allow-Credentials", "false"); // Changed to false
        responseContext.getHeaders().add(
                "Access-Control-Allow-Headers",
                "origin, content-type, accept, authorization");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }
}