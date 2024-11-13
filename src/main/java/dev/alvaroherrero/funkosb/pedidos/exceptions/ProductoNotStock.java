package dev.alvaroherrero.funkosb.pedidos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductoNotStock extends PedidoException {
    public ProductoNotStock(Long id) {
        super("Producto con id " + id + " no tiene stock suficiente");
    }
}
