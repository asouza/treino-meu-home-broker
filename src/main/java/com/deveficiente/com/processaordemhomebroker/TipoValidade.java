package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public enum TipoValidade {
    TON {
        @Override
        public Optional<Object> getDadosExtras(Map<String, String> dadosExtras) {
            return Optional.empty();
        }

		@Override
		public NovaOrdemLimitadaMessage criaNovaLimitadaMessage(
				Map<String, String> mensagem) {
			//não consegui me livrar desse acoplamento aqui.
			String ativo = mensagem.get("ativo");
			int quantidade = Integer.parseInt(mensagem.get("quantidade"));
			BigDecimal preco = new BigDecimal(mensagem.get("preco"));
			TipoOferta tipoOferta = TipoOferta.valueOf(mensagem.get("tipoOferta"));
			String codigoCorretota = mensagem.get("codigoCorretora");
			
			return new NovaOrdemLimitadaTonMessage(ativo,quantidade,codigoCorretota,tipoOferta,preco);
		}
    };

    abstract public Optional<Object> getDadosExtras(Map<String, String> dadosExtras);

	public static TipoValidade converte(String tipo) {
		try {
			return TipoValidade.valueOf(tipo);
		} catch (Exception e) {
			throw new IllegalArgumentException("O tipo "+tipo+" não é um TipoValidade suportado",e);
		}
	}

	public abstract NovaOrdemLimitadaMessage criaNovaLimitadaMessage(
			Map<String, String> mensagem);
}
