package dev.alvaroherrero.funkosb.model;

import dev.alvaroherrero.funkosb.model.funkocategory.FunkoCategory;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class Funko {
    private static final AtomicLong counter = new AtomicLong();
    private long id;
    private String name;
    private float price;
    private FunkoCategory category;
    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at = LocalDateTime.now();

    public Funko(String name, float price, FunkoCategory category) {
        this.id = counter.incrementAndGet();
        this.name = name;
        this.price = price;
        this.category = category;
    }

}