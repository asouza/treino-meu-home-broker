package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class NovaOrdemimitadaEOCRequest {

    @NotBlank
    private String ativo;
    @Min(1)
    private int quantidade;
    @NotBlank
    private String codigoCorretora;
    @NotNull
    private TipoOrdem tipoOrdem;
    @Positive
    private BigDecimal preco;

    NovaOrdemimitadaEOCRequest(@NotBlank String ativo, @Min(1) int quantidade, @NotBlank String codigoCorretora,
            @NotNull TipoOrdem tipoOrdem, @Positive BigDecimal preco) {
        this.ativo = ativo;
        this.quantidade = quantidade;
        this.codigoCorretora = codigoCorretora;
        this.tipoOrdem = tipoOrdem;
        this.preco = preco;
    }

    @Override
    public String toString() {
        return "NovaOrdemimitadaEOCRequest [ativo=" + ativo + ", quantidade=" + quantidade + ", codigoCorretora="
                + codigoCorretora + ", tipoOrdem=" + tipoOrdem + ", preco=" + preco + "]";
    }

    

}
