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
//20 icps (usando metrica default)
public class OrdemLimitada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Min(1)
    private int quantidade;
    @NotBlank
    private String codigoCorretora;
    @NotNull
    //1
    private TipoOferta tipoOferta;
    @ManyToOne
    //1
    private BookOfertas bookOfertas;
    private LocalDateTime instante;
    //1    
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
    //1    
	private Cliente cliente;
    
    @OneToMany(mappedBy = "origem",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    //1    
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

    //1
	public Resultado<RuntimeException, Void> executa(AcessoMelhoresOfertas melhoresOfertas) {
		Assert.isTrue(!this.foiExecutadaComSucesso()
				,"Não pode ter execucoes de sucesso para uma mesma ordem");		

		/*
		 * - precisa achar uma ordem de compra ou venda de um ativo com a 
		 *   mesma quantidade e que satisfaça o limite estabelecido
		 * - se achar, executa a operação (muda o status da ordem)
		 * - atualiza a carteira do cliente
		 */
		
		//mau sinal ? Dois this na mesma linha sempre me chama atencao
		
		//TODO #refactor aqui eu posso receber uma abstração para buscar as melhores ofertas de maneira otimizada
		
		Optional<OrdemLimitada> melhorOferta = melhoresOfertas.buscaMelhorOferta(this);
	    //1		
		if(melhorOferta.isPresent()) {
		    //1			
			ParExecucaoOrdem parExecucoes = this.criaOperacaoSucesso(melhorOferta.get());
			
			this.execucoes.add(parExecucoes.getOrigemExecucao());
			melhorOferta.get().execucoes.add(parExecucoes.getMatchExecucao());
			
			parExecucoes.atualizaClientes();
		    //1
			return Resultado.sucessoSemInfoAdicional();					
		}
		
		this.execucoes.add(ExecucaoOrdem.falha(this));
	    //1
		return Resultado.falhaCom(new NaoAchouMatchParaOrdemLimitadaException(this,"Não encontramos uma match para a ordem "+this.codigo));
	}
	
	private ParExecucaoOrdem criaOperacaoSucesso(OrdemLimitada match) {
		/*
		 * - crio a nova operacao de sucesso com a ordem atual, a outra, no instante
		 * - adiciono a operacao de sucesso na lista de operacoes da ordem
		 * - retorno a nova operacao
		 */
		
		
		Assert.isTrue(!this.foiExecutadaComSucesso()
				,"Não pode ter execucoes de sucesso para uma mesma ordem");
		
		//aqui não pode ser um set, pq eu talvez possa ter duas execucoes de falha
		
		//estou em dúvida do melhor nome para essas variaveis
		//e o melhor era pergunta se realmente são duas execucoes
		ExecucaoOrdem execucaoSucessoOrigem = ExecucaoOrdem.sucesso(this,match);
		ExecucaoOrdem execucaoSucessoMatch = ExecucaoOrdem.sucesso(match,this);
		
		return new ParExecucaoOrdem(execucaoSucessoOrigem,execucaoSucessoMatch);
		
		
	}

	public boolean isOposta(OrdemLimitada outraOrdem) {
	    //1
		return this.tipoOferta != outraOrdem.tipoOferta;
	}

	
	public boolean precoDentroDoLimite(OrdemLimitada outraOrdem) {
		Assert.isTrue(isOposta(outraOrdem), "Só faz sentido verificar limite para ordens opostas");
	    //1
		if(outraOrdem.tipoOferta == TipoOferta.compra) {
		    //1
			return this.preco.compareTo(outraOrdem.preco) <= 0;
		}
	    //1
		return this.preco.compareTo(outraOrdem.preco) >= 0;
	}
	
	//TODO #refactor Isso aqui pode realmente ser uma função
	public Comparator<OrdemLimitada> funcaoOrdenaPorMelhorPreco() {
		
		//se a ordem de compra, a funcao deve ordenar os precos do menor para o maior dentro do limite
		//se a ordem é de venda, a funcao deve ordenar os precos de compra
		
		return (o1,o2) -> {
				int ordemPreco = o1.getPreco().compareTo(o2.getPreco());
			    //1
				if(ordemPreco == 0) {
					return o1.getInstante().compareTo(o2.getInstante());					
				}
			    //1
				if(OrdemLimitada.this.tipoOferta == TipoOferta.compra) {
					//se for uma ordem de compra, a ordenacao precisa ser do menor para maior
					ordemPreco = ordemPreco * -1;
				}

				
				return ordemPreco;
			};
			
	}
		

	public boolean isCompra() {
	    //1
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
		    //1
			.filter(execucao -> execucao.isSucesso())
			.count();
		
	    //1
		Assert.isTrue(execucoesSucesso <= 1, "Uma ordem nunca poderia ser executada com sucesso mais de uma vez. Falha grave");
		
	    //1
		return execucoesSucesso == 1;
	}

	public Cliente getCliente() {
		return this.cliente;
	}

	public boolean quantidadeIgual(OrdemLimitada outraOrdem) {
		Assert.isTrue(isOposta(outraOrdem), "Só faz sentido verificar quantidade necessária para ordens opostas");
				
		return this.quantidade == outraOrdem.quantidade;
	}

	public BigDecimal calculaValorTotal() {
		return this.preco.multiply(new BigDecimal(this.quantidade));
	}

	public BookOfertas getBookOfertas() {
		return bookOfertas;
	}

	public TipoOferta getTipoOfertaOposta() {
		//delegando a chamada para mais perto do estado você tende a diminuir ifs
		return this.tipoOferta.oposta();
	}


}
