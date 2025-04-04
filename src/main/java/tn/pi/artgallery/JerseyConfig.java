package tn.pi.artgallery;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import tn.pi.artgallery.config.CORSFilter;
import tn.pi.artgallery.controller.ArtistController;
import tn.pi.artgallery.controller.ArtworkController;
import tn.pi.artgallery.controller.UserController;

@Configuration
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        packages("tn.pi.artgallery.controller");  // Remplace par le bon package

        // Enregistrement des contrôleurs
        register(ArtistController.class);
        register(ArtworkController.class);
        register(UserController.class);
        property("jersey.config.server.provider.classnames",
                "org.glassfish.jersey.media.multipart.MultiPartFeature");

        // Ajout du filtre CORS
        register(CORSFilter.class);
    }
}
