package com.deveficiente.com.processaordemhomebroker;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ListaOrdensBookOfertasController {
    
    private BookOfertasRepository bookOfertasRepository;

    public ListaOrdensBookOfertasController(BookOfertasRepository bookOfertasRepository) {
        this.bookOfertasRepository = bookOfertasRepository;
    }

    @GetMapping(value = "/api/book-ofertas/{ativo}")
    public List<Map<String,Object>> listaOrdens(@PathVariable("ativo") String ativo){
        Optional<BookOfertas> possivelBook = bookOfertasRepository.findByAtivo(ativo);

        if(possivelBook.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        BookOfertas bookOfertas = possivelBook.get();

        Function<OrdemLimitada,Map<String,Object>> geraJsonRetorno = ordem -> {
            return Map.of(
                "preco",ordem.getPreco()
                ,"quantidade",ordem.getQuantidade()
                ,"tipoValidade",ordem.getTipoValidade()
                ,"cliente",ordem.getCodigoCliente());
        };

        return bookOfertas.getOrdensPorInstante()
            .stream()
            .map(geraJsonRetorno)
            .collect(Collectors.toList());


    }
}
