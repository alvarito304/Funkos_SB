package dev.alvaroherrero.funkosb.pedidos.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

// Nombre de la colección en MongoDB
@Document("pedidos")
@TypeAlias("Pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pedido {
    // Id de mongo
    @Id
    @Builder.Default
    private ObjectId id = new ObjectId();

    @NotNull(message = "El id del usuario no puede ser nulo")
    private Long idUsuario;

    @NotNull(message = "El id del cliente no puede ser nulo")
    private Cliente cliente;

    @NotNull(message = "El pedido debe tener al menos una línea de pedido")
    private List<LineaPedido> lineasPedido;

    // Campos calculados
    @Builder.Default()
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default()
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder.Default()
    private Boolean isDeleted = false;

    @JsonProperty("id")
    public String get_id() {
        return id.toHexString();
    }

    // Método para calcular totalItems y total dinámicamente
    public void setLineasPedido(List<LineaPedido> lineasPedido) {
        this.lineasPedido = lineasPedido;
        // Calcula el total de items
        if (lineasPedido != null) {
            this.totalItems = lineasPedido.size();
            // Calcula el total del precio sumando los totales de cada línea
            this.total = lineasPedido.stream().mapToDouble(LineaPedido::getTotal).sum();
        } else {
            this.totalItems = 0;
            this.total = 0.0;
        }
    }

    // Campos calculados
    private Integer totalItems;
    private Double total;
}