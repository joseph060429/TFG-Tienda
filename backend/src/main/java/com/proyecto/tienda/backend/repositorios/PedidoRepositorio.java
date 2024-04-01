package com.proyecto.tienda.backend.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import com.proyecto.tienda.backend.models.PedidosModelo;

@Repository
public interface PedidoRepositorio extends MongoRepository<PedidosModelo, String> {

  // CONSULTA PERSONALIZADA PARA ENCONTRAR EL ÚLTIMO PEDIDO ORDENADO POR EL NÚMERO
  // DE PEDIDO DE FORMA DESCENDENTE
  @Query("{'numPedido' : {$exists : true}}")
  List<PedidosModelo> findTopByOrderByNumPedidoDesc();

  // CONSULTA DE BUSQUEDA POR ID DE PEDIDO
  Optional<PedidosModelo>  findBy_id(String _id);

  // CONSULTA PARA BUSCAR PEDIDOS POR ESTADO PENDIENTE
  List<PedidosModelo> findByEstado(String estado);

  // CONSULTA DE BUSQUEDA POR NUMERO DE PEDIDO
  Optional<PedidosModelo> findByNumPedido(Long numPedido);



    
  
}