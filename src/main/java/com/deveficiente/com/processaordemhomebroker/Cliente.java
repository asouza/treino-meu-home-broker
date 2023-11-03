package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;

import org.springframework.util.Assert;

import com.deveficiente.com.processaordemhomebroker.compartilhado.Resultado;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String codigo;

    //isso podia ser computado pelo histórico de creditos, compras e vendas.
    private BigDecimal saldoAtual = BigDecimal.ZERO;

    @OneToOne(mappedBy = "cliente",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	private Carteira carteira;
    
    @Deprecated
    public Cliente() {
    	
    }

    public Cliente(@NotBlank String codigo) {
        this.codigo = codigo;
        this.carteira = new Carteira(this);
    }

    public Resultado<RuntimeException, Void> podeRealizarOperacao(@Valid InfoOperacao infoOperacao) {
    	//tipico código que vaza encapsulamento checando estado por fora do objeto
    	//infoOperacao.getTipoOferta().equals(TipoOferta.compra)
        if(infoOperacao.isCompra()) {
            /*
             * verifica se tem saldo
             */
        	
        	/*
        	 * 
        	 * Primeira versão de código estava deixando a operacao dentro do cliente,
        	 * mas aí percebi que usei infoOperacao duas vezes na sequencia, essa densidade
        	 * me diz que a operacao deveria parar la. 
        	 */
        	
             if(this.saldoAtual.compareTo(infoOperacao.getValorTotal()) >= 0){
                return Resultado.sucessoSemInfoAdicional();
             }

             return Resultado.falhaCom(new SaldoInsuficienteException(infoOperacao,this));
        } 

    	
        // verifica se tem ativo na quantidade

        //aqui poderia ter uma interface que segmentasse mais a infooperacao
        return this.carteira.possuiAtivo(
        		infoOperacao.getAtivo(),
        		infoOperacao.getQuantidade());
  
  
        
    }

	public void realizaCredito(BigDecimal valor) {
		//aqui podia ser uma lista de creditos e debitos, como já falado.
		this.saldoAtual = this.saldoAtual.add(valor);
	}

	public BigDecimal getSaldoAtual() {
		return this.saldoAtual;
	}

	public String getCodigo() {
		return codigo;
	}

	public void atualizaCarteira(ExecucaoOrdem execucao) {
		Assert.isTrue(execucao.isSucesso(), "Não pode atualizar a carteira em funcao de uma execucao que falhou");
		
		//conseguir executar essa logica me indica qeu as coisas estão no lugar certo
		boolean podeRealizarOperacao = this.podeRealizarOperacao(new InfoOperacao() {
			
			@Override
			public TipoOferta getTipoOferta() {
				return execucao.getTipoOferta();
			}
			
			@Override
			public int getQuantidade() {
				return execucao.getQuantidade();
			}
			
			@Override
			public BigDecimal getPreco() {
				//como eu sei que foi sucesso, não preciso testar
				return execucao.getPreco().get();
			}
			
			@Override
			public String getAtivo() {
				return execucao.getAtivo();
			}
		}).isSucesso();
		
		Assert.isTrue(podeRealizarOperacao, "O cliente não tem condicoes de executar essa operacao. Esta chamada não devia ter acontecido");
		this.carteira.atualizaAtivos(execucao);
		
		if(execucao.isCompra()) {
			//quero restringir a atualizacao da carteira para uma execucao
			//poderia facilitar e passar uma ordem, um ativo. Quanto mais flexível, maior a chance de fazer caca
			this.saldoAtual = this.saldoAtual.subtract(execucao.calculaValorTotal().get());
		} else {
			this.saldoAtual = this.saldoAtual.add(execucao.calculaValorTotal().get());
		}
		
		
	}

}
