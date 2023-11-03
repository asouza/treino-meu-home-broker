package com.deveficiente.com.processaordemhomebroker;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{

    Cliente getByCodigo(String codigo);

}
