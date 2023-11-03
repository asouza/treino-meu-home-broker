package com.deveficiente.com.processaordemhomebroker;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class BookOfertas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "bookOfertas",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Set<OrdemLimitada> ordens = new HashSet<>();
    private String ativo;

    @Deprecated
    public BookOfertas() {
    }

    public BookOfertas(String ativo) {
        this.ativo = ativo;
    }

    public OrdemLimitada adiciona(Function<BookOfertas,OrdemLimitada> criaOrdem) {
        /*
         * Outra maneira era receber os dados da ordem e instanciar a ordem aqui passando o book
         */
        OrdemLimitada ordem = criaOrdem.apply(this);
        this.ordens.add(ordem);
        
        return ordem;
    }

    public String getAtivo() {
        return ativo;
    }

    public List<OrdemLimitada> getOrdensPorInstante() {
        return ordens
            .stream()
            .sorted((o1,o2) -> o1.getInstante().compareTo(o2.getInstante()))
            .collect(Collectors.toList());
    }




}
