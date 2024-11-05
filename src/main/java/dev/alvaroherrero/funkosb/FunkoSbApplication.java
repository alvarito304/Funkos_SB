package dev.alvaroherrero.funkosb;

import dev.alvaroherrero.funkosb.storage.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FunkoSbApplication {

    public static void main(String[] args) {
        SpringApplication.run(FunkoSbApplication.class, args);
    }

}
