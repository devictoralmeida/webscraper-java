package br.com.devictoralmeida.webscraper.java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "br.com.devictoralmeida.webscraper.java.repositories")
public class WebscraperJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebscraperJavaApplication.class, args);
    }

}
