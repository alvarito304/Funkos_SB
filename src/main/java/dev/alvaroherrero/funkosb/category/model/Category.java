package dev.alvaroherrero.funkosb.category.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import dev.alvaroherrero.funkosb.global.validations.validanotations.ValidCategory;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @ValidCategory
    private FunkoCategory category;

    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "category", orphanRemoval = true)
    @JsonManagedReference
    private List<Funko> funkos;

    private Boolean softDelete = false;
}
