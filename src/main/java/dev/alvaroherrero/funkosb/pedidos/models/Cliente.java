package dev.alvaroherrero.funkosb.pedidos.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record Cliente(
        @Length(min = 3, message = "El nombre debe tener al menos 3 caracteres")
        String nombreCompleto,
        @Email(message = "El email debe ser válido")
        String email,
        @NotBlank(message = "El teléfono no puede estar vacío")
        String telefono,
        @NotNull(message = "La dirección no puede ser nula")
        Direccion direccion
) {
}