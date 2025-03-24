package tn.pi.artgallery;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import tn.pi.artgallery.config.CORSFilter;

@Configuration
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(tn.pi.artgallery.controller.UserController.class);
        register(CORSFilter.class); // Ajouter le filtre CORS

    }
}
