package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;

public interface InfoOperacao {

    public int getQuantidade();

    public BigDecimal getPreco();

    public TipoOferta getTipoOferta();

    public String getAtivo();

    public default BigDecimal getValorTotal() {
        return this.getPreco().multiply(BigDecimal.valueOf(this.getQuantidade()));
    }

	public default boolean isCompra() {
		return this.getTipoOferta().equals(TipoOferta.compra);
	}


}
