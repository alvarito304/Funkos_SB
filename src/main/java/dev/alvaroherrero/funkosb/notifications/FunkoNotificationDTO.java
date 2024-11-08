package dev.alvaroherrero.funkosb.notifications;

import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FunkoNotificationDTO {
    private String nombre;
    private float precio;
    private FunkoCategory categoria;
}
