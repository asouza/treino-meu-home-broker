package com.deveficiente.com.processaordemhomebroker;

import jakarta.validation.constraints.Min;

public enum TipoOferta {
	venda(-1) {
		@Override
		TipoOferta oposta() {
			return compra;
		}
	}, 
	compra(1) {
		@Override
		TipoOferta oposta() {
			return venda;
		}
	};

	private int multiplicador;

	private TipoOferta(int multiplicador) {
		this.multiplicador = multiplicador;
	}

	int calculaQuantidade(@Min(1) int quantidade) {
		//se comprou, a quantidade é positiva. Se vendeu é negativa
		return quantidade * multiplicador;
	}

	abstract TipoOferta oposta();
}
