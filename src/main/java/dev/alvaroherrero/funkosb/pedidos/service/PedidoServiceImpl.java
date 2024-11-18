package dev.alvaroherrero.funkosb.pedidos.service;

import dev.alvaroherrero.funkosb.pedidos.models.LineaPedido;
import dev.alvaroherrero.funkosb.funko.service.IFunkoService;
import dev.alvaroherrero.funkosb.pedidos.exceptions.PedidoNoItems;
import dev.alvaroherrero.funkosb.pedidos.exceptions.PedidoNotFound;
import dev.alvaroherrero.funkosb.pedidos.exceptions.ProductoBadPrice;
import dev.alvaroherrero.funkosb.pedidos.exceptions.ProductoNotStock;
import dev.alvaroherrero.funkosb.pedidos.models.Pedido;
import dev.alvaroherrero.funkosb.pedidos.repository.IPedidosRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@CacheConfig(cacheNames = {"Pedidos"})
public class PedidoServiceImpl implements IPedidoService {

    private final IPedidosRepository pedidosRepository;
    private final IFunkoService funkoService;

    @Autowired
    public PedidoServiceImpl(IPedidosRepository pedidosRepository, IFunkoService funkoService) {
        this.pedidosRepository = pedidosRepository;
        this.funkoService = funkoService;
    }

    @Override
    public Page<Pedido> findAll(Pageable pageable) {
        log.info("Buscando todos los pedidos paginados");
        return pedidosRepository.findAll(pageable);
    }

    @Override
    @Cacheable(key = "#idPedido")
    public Pedido findById(ObjectId idPedido) {
        log.info("Buscando pedido con id {}", idPedido);
        return pedidosRepository.findById(idPedido).orElseThrow(() -> new PedidoNotFound(idPedido.toHexString()));
    }

    @Override
    @Cacheable(key = "#idUsuario")
    public Page<Pedido> findByIdUsuario(Long idUsuario, Pageable pageable) {
        log.info("Buscando todos los pedidos de un usuario con id {}", idUsuario);
        return pedidosRepository.findByIdUsuario(idUsuario, pageable);
    }

    @Override
    //@Cacheable(key = "#result.id.toHexString()")
    public Pedido save(Pedido pedido) {
        log.info("Guardando pedido {}", pedido);
        checkPedido(pedido);
        var pedidoReservado = reserveStock(pedido);
        pedidoReservado.setUpdatedAt(LocalDateTime.now());
        return pedidosRepository.save(pedido);
    }

    @Override
    @CacheEvict(key = "#idPedido")
    public void delete(ObjectId idPedido) {
        log.info("Eliminando pedido con id {}", idPedido);
        var pedido = findById(idPedido);
        returnStockPedido(pedido);
        pedidosRepository.delete(pedido);
    }

    @Override
    public Pedido update(ObjectId idPedido, Pedido pedido) {
        log.info("Actualizando pedido con id {}", idPedido);
        var pedidoActual = findById(idPedido);
        returnStockPedido(pedidoActual);
        checkPedido(pedido);
        var pedidoReservado = reserveStock(pedido);
        pedidoReservado.setUpdatedAt(LocalDateTime.now());
        return pedidosRepository.save(pedidoReservado);
    }

    public void checkPedido(Pedido pedido) {
        log.info("Comprobando pedido: {}", pedido);
        if (pedido.getLineasPedido() == null || pedido.getLineasPedido().isEmpty()) {
            throw new PedidoNoItems(pedido.getId().toHexString());
        }
        pedido.getLineasPedido().forEach(lineaPedido -> {
            var funko = funkoService.getFunkoById(lineaPedido.getIdProducto());
            // Si existe, comprobamos si hay stock
            if (funko.getStock() < lineaPedido.getCantidad()) {
                throw new ProductoNotStock(lineaPedido.getIdProducto());
            }
            // Podemos comprobar más cosas, como si el precio es el mismo, etc...
            if (Math.abs(funko.getPrice() - lineaPedido.getPrecioProducto()) > 0.0001) {
                throw new ProductoBadPrice(lineaPedido.getIdProducto());
            }
        });
    }

    public Pedido reserveStock(Pedido pedido) {
        log.info("Reservando stock para el pedido: {}", pedido);

        if (pedido.getLineasPedido() == null || pedido.getLineasPedido().isEmpty()) {
            throw new PedidoNoItems(pedido.getId().toHexString());
        }

        pedido.getLineasPedido().forEach(lineaPedido -> {
            var funko = funkoService.getFunkoById(lineaPedido.getIdProducto());
            funko.setStock(funko.getStock() - lineaPedido.getCantidad());
            funkoService.updateFunko(funko.getId(), funko);
            lineaPedido.setTotal((double) (lineaPedido.getCantidad() * funko.getPrice()));
        });
        var total = pedido.getLineasPedido().stream()
                .map(lineaPedido -> lineaPedido.getCantidad() * lineaPedido.getPrecioProducto())
                .reduce(0.0, Double::sum);
        var totalItems = pedido.getLineasPedido().stream()
                .map(LineaPedido::getCantidad)
                .reduce(0, Integer::sum);
        pedido.setTotal(total);
        pedido.setTotalItems(totalItems);
        return pedido;
    }
    public Pedido returnStockPedido(Pedido pedido) {
        log.info("Retornando stock del pedido: {}", pedido);
        if (pedido.getLineasPedido() != null) {
            pedido.getLineasPedido().forEach(lineaPedido -> {
                var funko = funkoService.getFunkoById(lineaPedido.getIdProducto());
                funko.setStock(funko.getStock() + lineaPedido.getCantidad());
                funkoService.updateFunko(funko.getId(), funko);
            });
        }
        return pedido;
    }

}
