package org.neewrobert.jumbo;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "org.neewrobert.jumbo.adapter.out.persistence")
public class JumboLocatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(JumboLocatorApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Jumbo Store Locator API")
                        .version("1.0")
                        .description("API for finding the closest Jumbo stores based on coordinates"));
    }

}
