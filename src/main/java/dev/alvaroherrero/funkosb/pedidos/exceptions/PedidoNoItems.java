package dev.alvaroherrero.funkosb.pedidos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PedidoNoItems extends PedidoException {
    public PedidoNoItems(String id) {
        super("Pedido con id " + id + " no contiene items para ser procesado");
    }

}