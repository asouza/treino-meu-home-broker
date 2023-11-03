package com.deveficiente.com.processaordemhomebroker;

import java.util.Map;

public enum TipoOrdem {

    limitada {
        @Override
        public Object getDadosExtras(Map<String, String> dadosExtras) {
            return new DadosExtrasOrdemLimitada(dadosExtras);
        }
    };

    abstract public Object getDadosExtras(Map<String, String> dadosExtras);
}
