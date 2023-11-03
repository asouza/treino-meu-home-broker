package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;

public interface NovaOrdemLimitadaMessage {
	
	String getAtivo();
	
	int getQuantidade();
	
	String codigoCorretora();
	
	TipoOferta getTipoOferta();
	
	BigDecimal getPreco();
	
	OrdemLimitada toModel(BookOfertas bookOfertas, Cliente cliente);
	
}
