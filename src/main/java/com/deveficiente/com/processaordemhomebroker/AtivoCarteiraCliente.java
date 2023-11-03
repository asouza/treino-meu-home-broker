package com.deveficiente.com.processaordemhomebroker;

import jakarta.persistence.Embeddable;

@Embeddable
public class AtivoCarteiraCliente {

	private String codigo;
	private int quantidade;
	
	@Deprecated
	public AtivoCarteiraCliente() {
		// TODO Auto-generated constructor stub
	}

	public AtivoCarteiraCliente(String codigo, int quantidade) {
		super();
		this.codigo = codigo;
		this.quantidade = quantidade;
	}

	public boolean isMesmo(String codigo) {
		return this.codigo.equals(codigo);
	}

	public boolean temQuantidade(int quantidade) {
		return this.quantidade >= quantidade;
	}
		

}
