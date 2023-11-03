package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class Ordem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Min(1)
    private int quantidade;
    @NotBlank
    private String codigoCorretora;
    @NotNull
    private TipoOferta tipoOferta;
    @ManyToOne
    private BookOfertas bookOfertas;
    private LocalDateTime instante;
    private TipoOrdem tipoOrdem;
    private TipoValidade tipoValidade;

    @ElementCollection
    @MapKeyColumn(name = "chave")
    @Column(name = "valor")
    private Map<String, String> dadosExtrasTipoOrdem = new HashMap<>();    

    @ElementCollection
    @MapKeyColumn(name = "chave")
    @Column(name = "valor")
    private Map<String, String> dadosExtrasTipoValidade = new HashMap<>();        

    @Deprecated
    public Ordem(){

    }

    private Ordem(BookOfertas bookOfertas,TipoOrdem tipoOrdem,TipoValidade tipoValidade,int quantidade, String codigoCorretora, TipoOferta tipoOferta, Map<String, String> dadosExtrasTipoOrdem, Map<String, String> dadosExtrasTipoValidade) {
        this.bookOfertas = bookOfertas;
        this.tipoOrdem = tipoOrdem;
        this.tipoValidade = tipoValidade;
        this.quantidade = quantidade;
        this.codigoCorretora = codigoCorretora;
        this.tipoOferta = tipoOferta;
        this.instante = LocalDateTime.now();
        this.dadosExtrasTipoOrdem = dadosExtrasTipoOrdem;
        this.dadosExtrasTipoValidade = dadosExtrasTipoValidade;
    }

    public String getAtivo() {
        return bookOfertas.getAtivo();
    }

    public LocalDateTime getInstante() {
        return instante;
    }

    public static Ordem novaLimitadaTON(BookOfertas bookOfertas, @Min(1) int quantidade,
            @NotBlank String codigoCorretora, @NotNull TipoOferta tipoOrdem, @Positive BigDecimal preco) {
        Map<String,String> dadosExtrasTipoOrdem = Map.of("preco",preco.toString());
        Map<String,String> dadosExtrasTipoValidade = new HashMap<>();

        return new Ordem(bookOfertas,TipoOrdem.limitada,TipoValidade.TON ,quantidade, codigoCorretora, tipoOrdem,dadosExtrasTipoOrdem,dadosExtrasTipoValidade);
    }
    
    /**
     * 
     * @param <T>
     * @return o objeto que representa os dados extras da ordem de determinado tipo
     */
    public <T> T getDadosExtrasTipoOrdem() {                
        return (T) this.tipoOrdem.getDadosExtras(this.dadosExtrasTipoOrdem);
    }

    /**
     * 
     * @param <T>
     * @return o objeto que representa os dados extras da ordem de determinado tipo
     */
    public <T> T getDadosExtrasTipoValidade() {                
        return (T) this.tipoValidade.getDadosExtras(this.dadosExtrasTipoValidade);
    }    
}
