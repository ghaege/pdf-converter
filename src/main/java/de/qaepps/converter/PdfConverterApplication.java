package de.qaepps.converter;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PdfConverterApplication extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(PdfConverterApplication.class, args);
  }

  @Bean
  public OpenAPI pdfConverterOpenAPI() {
    return new OpenAPI()
        .info(new Info().title("PdfConversion REST API")
            .description("PdfConversion REST API")
            .version("v1.0.0")
            .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
  }
}
