package dev.alvaroherrero.funkosb.pedidos.service;

import dev.alvaroherrero.funkosb.pedidos.models.Pedido;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPedidoService {
    Page<Pedido> findAll(Pageable pageable);

    Pedido findById(ObjectId idPedido);

    Page<Pedido> findByIdUsuario(Long idUsuario, Pageable pageable);

    Pedido save(Pedido pedido);

    void delete(ObjectId idPedido);

    Pedido update(ObjectId idPedido, Pedido pedido);
}
