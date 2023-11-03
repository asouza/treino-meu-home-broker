package com.deveficiente.com.processaordemhomebroker;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class BookOfertas {

    @OneToMany(mappedBy = "bookOfertas",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Set<Ordem> ordens = new HashSet<>();
    private String ativo;

    public BookOfertas(String ativo) {
        this.ativo = ativo;
    }

    public Ordem adiciona(Function<BookOfertas,Ordem> criaOrdem) {
        /*
         * Outra maneira era receber os dados da ordem e instanciar a ordem aqui passando o book
         */
        Ordem ordem = criaOrdem.apply(this);
        this.ordens.add(ordem);
        
        return ordem;
    }

    public String getAtivo() {
        return ativo;
    }





}
