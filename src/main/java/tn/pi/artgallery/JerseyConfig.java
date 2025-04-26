package tn.pi.artgallery;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import tn.pi.artgallery.config.CORSFilter;

@Configuration
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(tn.pi.artgallery.controller.UserController.class);
        register(tn.pi.artgallery.controller.EventController.class);
        register(tn.pi.artgallery.controller.EventPaymentController.class);
        register(CORSFilter.class);

    }
}
