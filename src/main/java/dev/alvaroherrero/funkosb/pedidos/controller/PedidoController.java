package dev.alvaroherrero.funkosb.pedidos.controller;


import dev.alvaroherrero.funkosb.global.pageresponse.PageResponse;
import dev.alvaroherrero.funkosb.pedidos.models.Pedido;
import dev.alvaroherrero.funkosb.pedidos.service.IPedidoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
@Slf4j
public class PedidoController {
    private final IPedidoService pedidoService;

    @Autowired
    public PedidoController(IPedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<Pedido>> getAllPedidos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        log.info("Obteniendo todos los pedidos");
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        var pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(pedidoService.findAll(pageable), sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedido(@PathVariable("id") ObjectId idPedido) {
        log.info("Obteniendo pedido con id: " + idPedido);
        return ResponseEntity.ok(pedidoService.findById(idPedido));
    }
    @GetMapping("/usuario/{id}")
    public ResponseEntity<PageResponse<Pedido>> getPedidosByUsuario(
            @PathVariable("id") Long idUsuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Obteniendo pedidos del usuario con id: " + idUsuario);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(pedidoService.findByIdUsuario(idUsuario, pageable), sortBy, direction));
    }
    @PostMapping()
    public ResponseEntity<Pedido> createPedido(@Valid @RequestBody Pedido pedido) {
        log.info("Creando pedido: " + pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.save(pedido));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> updatePedido(@PathVariable("id") ObjectId idPedido, @Valid @RequestBody Pedido pedido) {
        log.info("Actualizando pedido con id: " + idPedido);
        return ResponseEntity.ok(pedidoService.update(idPedido, pedido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Pedido> deletePedido(@PathVariable("id") ObjectId idPedido) {
        log.info("Borrando pedido con id: " + idPedido);
        pedidoService.delete(idPedido);
        return ResponseEntity.noContent().build();
    }
}
