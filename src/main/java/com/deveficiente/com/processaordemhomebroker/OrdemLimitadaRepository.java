package com.deveficiente.com.processaordemhomebroker;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemLimitadaRepository extends JpaRepository<OrdemLimitada, Long>{

	OrdemLimitada getByCodigo(UUID codigo);

}
