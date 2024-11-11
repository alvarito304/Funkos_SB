package dev.alvaroherrero.funkosb.funko.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.alvaroherrero.funkosb.category.model.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
    public long id;

    @Column(nullable = false)
    @NotEmpty
    private String name;

    @Column(nullable = false)
    @Min(value = 1, message = "El precio debe ser mayor que 0")
    private float price;

   @ManyToOne
   @JoinColumn(name = "category_id", nullable = false)
   @JsonBackReference
    private Category category;

   private Boolean funkoSoftDeleted = false;

   private String image;

    @CreatedDate
    private LocalDateTime created_at = LocalDateTime.now();
    @LastModifiedDate
    private LocalDateTime updated_at = LocalDateTime.now();


    public Funko(String name, float price, Category category ) {
        this.category = category;
        this.price = price;
        this.name = name;
    }
}