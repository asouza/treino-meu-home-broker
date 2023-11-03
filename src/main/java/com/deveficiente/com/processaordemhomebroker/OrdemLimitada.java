package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
public class OrdemLimitada {

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
    private TipoValidade tipoValidade;

    @ElementCollection
    @MapKeyColumn(name = "chave")
    @Column(name = "valor")
    private Map<String, String> dadosExtrasTipoValidade = new HashMap<>();
    @NotNull
    @Positive
    private BigDecimal preco;
    @NotNull
	private UUID codigo;
    @ManyToOne
    @NotNull
	private Cliente cliente;        

    @Deprecated
    public OrdemLimitada(){

    }

    private OrdemLimitada(BookOfertas bookOfertas,Cliente cliente, TipoValidade tipoValidade,BigDecimal preco ,int quantidade, String codigoCorretora, TipoOferta tipoOferta, Map<String, String> dadosExtrasTipoValidade) {
        this.bookOfertas = bookOfertas;
        this.cliente = cliente;
        this.tipoValidade = tipoValidade;
        this.preco = preco;
        this.quantidade = quantidade;
        this.codigoCorretora = codigoCorretora;
        this.tipoOferta = tipoOferta;
        this.instante = LocalDateTime.now();
        this.dadosExtrasTipoValidade = dadosExtrasTipoValidade;
        this.codigo = UUID.randomUUID();
    }

    public UUID getCodigo() {
		return codigo;
	}
    
    public String getAtivo() {
        return bookOfertas.getAtivo();
    }

    public LocalDateTime getInstante() {
        return instante;
    }

    public static OrdemLimitada novaLimitadaTON(BookOfertas bookOfertas, Cliente cliente, @Min(1) int quantidade,
            @NotBlank String codigoCorretora, @NotNull TipoOferta tipoOrdem, @Positive BigDecimal preco) {
        Map<String,String> dadosExtrasTipoValidade = new HashMap<>();

        return new OrdemLimitada(bookOfertas,cliente,TipoValidade.TON ,preco,quantidade, codigoCorretora, tipoOrdem,dadosExtrasTipoValidade);
    }
    
    /**
     * 
     * @param <T>
     * @return o objeto que representa os dados extras da ordem de determinado tipo
     */
    public <T> T getDadosExtrasTipoValidade() {                
        return (T) this.tipoValidade.getDadosExtras(this.dadosExtrasTipoValidade);
    }

    public BigDecimal getPreco() {
        return this.preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public TipoValidade getTipoValidade() {
        return tipoValidade;
    }

    public Long getId() {
        return id;
    }

	public String getCodigoCliente() {
		return this.cliente.getCodigo();
	}


}
