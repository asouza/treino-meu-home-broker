package com.deveficiente.com.processaordemhomebroker;

import java.util.Map;
import java.util.Optional;

public enum TipoValidade {
    TON {
        @Override
        public Optional<Object> getDadosExtras(Map<String, String> dadosExtras) {
            return Optional.empty();
        }
    };

    abstract public Optional<Object> getDadosExtras(Map<String, String> dadosExtras);
}
