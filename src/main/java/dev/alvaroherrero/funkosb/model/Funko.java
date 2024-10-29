package dev.alvaroherrero.funkosb.model;

import dev.alvaroherrero.funkosb.model.funkocategory.FunkoCategory;
import dev.alvaroherrero.funkosb.validations.validanotations.ValidCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EntityListeners( AuditingEntityListener.class)
public class Funko {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false)
    @NotEmpty
    private String name;

    @Column(nullable = false)
    @Min(value = 1, message = "El precio debe ser mayor que 0")
    private float price;

    @Column(nullable = false)
    @ValidCategory
    private FunkoCategory category;

    @CreatedDate
    private LocalDateTime created_at = LocalDateTime.now();
    @LastModifiedDate
    private LocalDateTime updated_at = LocalDateTime.now();

    public Funko(String name, float price, FunkoCategory category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

}