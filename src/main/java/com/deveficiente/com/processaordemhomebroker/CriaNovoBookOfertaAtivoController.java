package com.deveficiente.com.processaordemhomebroker;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.constraints.NotBlank;

@RestController
public class CriaNovoBookOfertaAtivoController {
    
    private BookOfertasRepository bookOfertasRepository;

    public CriaNovoBookOfertaAtivoController(BookOfertasRepository bookOfertasRepository) {
        this.bookOfertasRepository = bookOfertasRepository;
    }

    @PostMapping(value = "/api/book-ofertas")
    public void criaNovoBookOfertaAtivo(@NotBlank String ativo){

        Optional<BookOfertas> possivelBook = bookOfertasRepository.findByAtivo(ativo);

        if(possivelBook.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
            

        this.bookOfertasRepository.save(new BookOfertas(ativo));
    }
}
