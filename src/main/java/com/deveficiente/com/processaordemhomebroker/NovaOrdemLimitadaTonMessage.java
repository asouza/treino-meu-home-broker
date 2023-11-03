package com.deveficiente.com.processaordemhomebroker;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class NovaOrdemLimitadaTonMessage {

    @NotBlank
    private String ativo;
    @Min(1)
    private int quantidade;
    @NotBlank
    private String codigoCorretora;
    @NotNull
    private TipoOferta tipoOrdem;
    @Positive
    private BigDecimal preco;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    NovaOrdemLimitadaTonMessage(@NotBlank @JsonProperty("ativo") String ativo, @Min(1) @JsonProperty("quantidade") int quantidade, @NotBlank  @JsonProperty("codigoCorretora") String codigoCorretora,
            @NotNull @JsonProperty("tipoOrdem") TipoOferta tipoOrdem, @Positive @JsonProperty("preco") BigDecimal preco) {
        this.ativo = ativo;
        this.quantidade = quantidade;
        this.codigoCorretora = codigoCorretora;
        this.tipoOrdem = tipoOrdem;
        this.preco = preco;
    }

    public String getAtivo() {
        return ativo;
    }

    @Override
    public String toString() {
        return "NovaOrdemLimitadaTonMessage [ativo=" + ativo + ", quantidade=" + quantidade + ", codigoCorretora="
                + codigoCorretora + ", tipoOrdem=" + tipoOrdem + ", preco=" + preco + "]";
    }

    public Ordem toModel(BookOfertas bookOfertas) {
        //aqui tem que usar um builder para criar a ordem considerando a especificidade
        //Ordem.novaLimitadaTON(bookOfertas, quantidade, codigoCorretora, tipoOrdem, preco)
        //precisa enriquecer a ordem com o tipo dela(mercado,limitada etc,), validadade e infos especificas.
        //Se for limitada -> tem preco
        return Ordem.novaLimitadaTON(bookOfertas, quantidade, codigoCorretora, tipoOrdem, preco);
    }

    public BigDecimal getPreco() {
        return preco;
    }

}
