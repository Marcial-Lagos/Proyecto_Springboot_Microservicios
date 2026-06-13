package cl.duoc.msauth.config;

import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.EntityModel;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    static {
        // Soporte para que Swagger no se caiga con HATEOAS
        SpringDocUtils.getConfig().replaceWithClass(EntityModel.class, Object.class);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de autentificion")
                        .version("1.0")
                        .description("Api que entrega el token JWT"));
    }
}