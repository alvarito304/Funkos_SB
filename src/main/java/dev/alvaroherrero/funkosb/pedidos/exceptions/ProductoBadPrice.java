package dev.alvaroherrero.funkosb.pedidos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductoBadPrice extends PedidoException {
    public ProductoBadPrice(Long id) {
        super("Producto con id " + id + " no tiene un precio válido o no coincide con su precio actual");
    }
}