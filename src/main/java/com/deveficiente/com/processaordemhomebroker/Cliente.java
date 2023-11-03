package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;

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

    @OneToOne(mappedBy = "cliente",cascade = CascadeType.PERSIST)
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

}
