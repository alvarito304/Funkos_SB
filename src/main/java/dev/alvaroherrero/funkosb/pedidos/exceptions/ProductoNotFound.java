package dev.alvaroherrero.funkosb.pedidos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductoNotFound extends PedidoException {
    public ProductoNotFound(Long id) {
        super("Producto con id " + id + " no encontrado");
    }

    public ProductoNotFound(UUID uuid) {
        super("Producto con uuid " + uuid + " no encontrado");
    }

}