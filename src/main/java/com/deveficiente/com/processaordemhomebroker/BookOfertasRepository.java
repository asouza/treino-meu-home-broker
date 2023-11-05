package com.deveficiente.com.processaordemhomebroker;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
public interface BookOfertasRepository extends JpaRepository<BookOfertas, Long>{

    Optional<BookOfertas> findByAtivo(@NotBlank String ativo);
    
    

}
