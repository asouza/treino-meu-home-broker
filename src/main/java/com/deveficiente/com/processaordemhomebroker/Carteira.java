package com.deveficiente.com.processaordemhomebroker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.deveficiente.com.processaordemhomebroker.compartilhado.Resultado;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Carteira {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    @ElementCollection
    private List<AtivoCarteiraCliente> ativos = new ArrayList<>();
    @OneToOne
    private Cliente cliente;
    
    
    @Deprecated
    public Carteira() {
    	
    }
        

    public Carteira(Cliente cliente) {
		super();
		this.cliente = cliente;
	}



	public Resultado<RuntimeException, Void> possuiAtivo(String codigoAtivo, int quantidade) {
		//primeiro tem que juntar todos os ativos iguais
    	boolean temAtivo = ativos
            .stream()
            .filter(ativo -> ativo.isMesmo(codigoAtivo))            
            .filter(ativo -> ativo.temQuantidade(quantidade))
            .findFirst()
            .isPresent();
    	
    	if(temAtivo) {
    		return Resultado.sucessoSemInfoAdicional();
    	}
    	
    	return Resultado.falhaCom(new AtivoInsuficienteException(codigoAtivo,quantidade));
                
    }


	public void atualizaAtivos(ExecucaoOrdem execucao) {
		//aqui talvez o melhor fosse deixar bem nitida a regra de execucao (que no caso é registrar a perda ou adicao de ativos)
		this.ativos.add(execucao.toAtivo());
	}

}
