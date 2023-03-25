package de.qaepps.converter;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@SpringBootApplication
public class PdfConverterApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(PdfConverterApplication.class, args);
    }
    
    @Configuration
    public class SwaggerConfig {
        
    	@Bean
        public Docket api() {
            return new Docket(DocumentationType.SWAGGER_2)
                    .useDefaultResponseMessages(false)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("de.qaepps.converter.web"))
                    .build()
                    .apiInfo(apiInfo());
        }

        private ApiInfo apiInfo() {
            return new ApiInfo(
                    "PdfConversion REST API",
                    "PdfConversion REST API for Online conversion. Automates conversions from office document formats to pdf using JODconverter, LibreOffice or Apache OpenOffice.",
                    "0.1",
                    "Terms of service",
                    new Contact("Gerhard Haege", "https://www.qaepps.de", null),
                    "Apache License Version 2.0",
                    "https://www.apache.org/licenses/LICENSE-2.0",
                    Collections.emptyList());
        }
    }
}
