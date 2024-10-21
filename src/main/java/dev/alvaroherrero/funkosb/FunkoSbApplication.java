package dev.alvaroherrero.funkosb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FunkoSbApplication {

    public static void main(String[] args) {
        SpringApplication.run(FunkoSbApplication.class, args);
    }

}
