package com.deveficiente.com.processaordemhomebroker;

import jakarta.validation.Valid;

public class SaldoInsuficienteException extends RuntimeException {

    private @Valid InfoOperacao infoOperacao;
    private Cliente cliente;

    public SaldoInsuficienteException(@Valid InfoOperacao infoOperacao, Cliente cliente) {
        this.infoOperacao = infoOperacao;
        this.cliente = cliente;
    }

}
