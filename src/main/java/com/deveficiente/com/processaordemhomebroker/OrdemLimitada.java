package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import com.deveficiente.com.processaordemhomebroker.compartilhado.Resultado;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
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
    
    @OneToMany(mappedBy = "origem",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	private List<ExecucaoOrdem> execucoes = new ArrayList<>();        

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

	public boolean pertenceAAtivo(String ativo) {
		return this.bookOfertas.pertenceAAtivo(ativo);
	}

	public Resultado<RuntimeException, Void> executa() {
		//se já foi executada, não pode de novo

		/*
		 * - precisa achar uma ordem de compra ou venda de um ativo com a 
		 *   mesma quantidade e que satisfaça o limite estabelecido
		 * - se achar, executa a operação (muda o status da ordem)
		 * - atualiza a carteira do cliente
		 */
		
		//mau sinal ? Dois this na mesma linha sempre me chama atencao
		
		Optional<OrdemLimitada> melhorOferta = this.bookOfertas.buscaMelhorOfertaLimitada(this);
		if(melhorOferta.isPresent()) {
			ExecucaoOrdem execucao = this.adicionaOperacaoSucesso(melhorOferta.get());
			this.cliente.atualizaCarteira(execucao);
			return Resultado.sucessoSemInfoAdicional();					
		}
		
		return Resultado.falhaCom(new NaoAchouMatchParaOrdemLimitadaException(this,"Não encontramos uma match para a ordem "+this.codigo));
	}
	
	private ExecucaoOrdem adicionaOperacaoSucesso(OrdemLimitada ordemLimitada) {
		/*
		 * - crio a nova operacao de sucesso com a ordem atual, a outra, no instante
		 * - adiciono a operacao de sucesso na lista de operacoes da ordem
		 * - retorno a nova operacao
		 */
		
		
		Assert.isTrue(!this.foiExecutadaComSucesso()
				,"Não pode ter execucoes de sucesso para uma mesma ordem");
		
		//aqui não pode ser um set, pq eu talvez possa ter duas execucoes de falha
		ExecucaoOrdem execucaoSucesso = ExecucaoOrdem.sucesso(this,ordemLimitada);
		this.execucoes.add(execucaoSucesso);
		
		return execucaoSucesso;
		
		
	}

	public boolean isOposta(OrdemLimitada outraOrdem) {
		return this.tipoOferta != outraOrdem.tipoOferta;
	}

	
	public boolean precoDentroDoLimite(OrdemLimitada outraOrdem) {
		Assert.isTrue(isOposta(outraOrdem), "Só faz sentido verificar limite para ordens opostas");
		
		if(outraOrdem.tipoOferta == TipoOferta.compra) {
			return this.preco.compareTo(outraOrdem.preco) <= 0;
		}
		return this.preco.compareTo(outraOrdem.preco) >= 0;
	}
	
	public Comparator<OrdemLimitada> funcaoOrdenaPorMelhorPreco() {
		
		//se a ordem de compra, a funcao deve ordenar os precos do menor para o maior dentro do limite
		//se a ordem é de venda, a funcao deve ordenar os precos de compra
		
		return (o1,o2) -> {
				int ordemPreco = o1.getPreco().compareTo(o2.getPreco());
				
				if(ordemPreco == 0) {
					return o1.getInstante().compareTo(o2.getInstante());					
				}
				
				if(OrdemLimitada.this.tipoOferta == TipoOferta.compra) {
					//se for uma ordem de compra, a ordenacao precisa ser do menor para maior
					ordemPreco = ordemPreco * -1;
				}

				
				return ordemPreco;
			};
			
	}
		

	public boolean isCompra() {
		return this.tipoOferta == TipoOferta.compra;
	}

	public AtivoCarteiraCliente toAtivo() {		
		return new AtivoCarteiraCliente(this.bookOfertas.getAtivo(), this.tipoOferta.calculaQuantidade(this.quantidade));
	}

	public TipoOferta getTipoOferta() {
		return this.tipoOferta;
	}

	public boolean foiExecutadaComSucesso() {
		long execucoesSucesso = this
			.execucoes
			.stream()
			.filter(execucao -> execucao.isSucesso())
			.count();
		
		Assert.isTrue(execucoesSucesso <= 1, "Uma ordem nunca poderia ser executada com sucesso mais de uma vez. Falha grave");
		
		return execucoesSucesso == 1;
	}


}
